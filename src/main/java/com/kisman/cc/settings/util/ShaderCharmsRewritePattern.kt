package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.ShaderCharmsRewriteSetting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.collections.Bind
import com.kisman.cc.util.enums.ShaderCharmsOverlaying
import com.kisman.cc.util.enums.ShaderCharmsRewriteObjectTypes
import com.kisman.cc.util.enums.ShaderCharmsRewriteSettingTypes
import com.kisman.cc.util.enums.ShaderCharmsRewriteShaders
import com.kisman.cc.util.enums.dynamic.ShaderCharmsObjectsEnum
import com.kisman.cc.util.render.shader.uniform.type.types.TypeBool
import com.kisman.cc.util.render.shader.uniform.type.types.TypeFloat
import com.kisman.cc.util.render.shader.uniform.type.types.TypeInt
import com.kisman.cc.util.thread.kisman.suppliers.ThreaddedBooleanSupplier
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity

/**
 * @author _kisman_
 * @since 13:28 of 16.08.2022
 */
class ShaderCharmsRewritePattern(
    private val module : Module
) {
    private val groups = ArrayList<SettingGroup>()
    private val settings = ArrayList<ShaderCharmsRewriteSetting>()

    private val optimizationGroup = SettingGroup(Setting("Optimization", module))
    private val listProcessing = optimizationGroup.add(Setting("List Processing", module, false))

    private val itemsFix = Setting("Items Fix", module, false)

    private val overlaying = Setting("Overlaying", module, ShaderCharmsOverlaying.Post)

    private var needToRenderEntities = ThreaddedBooleanSupplier { false }
    private var needToRenderTileEntities = ThreaddedBooleanSupplier { false }
    private var needToRenderHand = ThreaddedBooleanSupplier { false }

    private var criticalSection = false

    private val shaderMap = HashMap<
            ShaderCharmsRewriteShaders,
            HashMap<
                    ShaderCharmsObjectsEnum.ShaderCharmsRewriteObjects,
                    ThreaddedBooleanSupplier
            >
    >()

    /*private val uniforms = ArrayList<
            Bind<
                    Bind<
                            Int,
                            ShaderCharmsRewriteShaders
                            >,
                    ArrayList<
                            Bind<
                                    Int,
                                    ShaderCharmsRewriteSetting
                                    >
                            >
                    >
            >()*/

    val uniforms = HashMap<
            Int,
            Bind<
                    ShaderCharmsRewriteShaders,
                    HashMap<
                            Int,
                            ShaderCharmsRewriteSetting
                    >
            >
    >()

    private val validEntities = HashMap<
            Entity,
            ArrayList<ShaderCharmsRewriteShaders>
    >()


    private val validTileEntities = HashMap<
            TileEntity,
            ArrayList<ShaderCharmsRewriteShaders>
    >()

    /*private val framebuffers = ArrayList<
            Bind<
                    Bind<
                            Int
                            >
                    >
            >()*/

    init {
        for(type in ShaderCharmsRewriteObjectTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))

            groups.add(group)

            for(type1 in ShaderCharmsObjectsEnum.ShaderCharmsRewriteObjects.byType(type)) {
                val typeGroup = group.add(SettingGroup(Setting(type1.name, module)))
                val shadersGroup = typeGroup.add(SettingGroup(Setting("Shaders", module)))

                groups.add(typeGroup)
                groups.add(shadersGroup)

                settings.add(ShaderCharmsRewriteSetting(
                    typeGroup.add(Setting(
                        "$type $type1 State",
                        module,
                        false
                    ).setTitle("State")),
                    type,
                    type1,
                    null,
                    ShaderCharmsRewriteSettingTypes.StateOfOption,
                    -1
                ))

                for(shader in ShaderCharmsRewriteShaders.values()) {
                    settings.add(ShaderCharmsRewriteSetting(
                        shadersGroup.add(Setting(
                            "$type $type1 ${shader.displayName} State",
                            module,
                            false
                        ).setTitle(shader.displayName)),
                        type,
                        type1,
                        shader,
                        ShaderCharmsRewriteSettingTypes.ShaderStateOfOption,
                        -1
                    ))

                    val shaderGroup = typeGroup.add(SettingGroup(Setting(shader.displayName, module)))

                    val uniformsMap = HashMap<Int, ShaderCharmsRewriteSetting>()

                    for(uniform in shader.uniforms) {
                        if(uniform.settingName != null) {
                            var uniformSetting: Setting?

                            if (uniform.get() is TypeInt) {
                                uniformSetting = Setting(
                                    "${type.name} ${type1.name} ${shader.displayName} ${uniform.settingName}",
                                    module,
                                    (uniform.get().get() as Int).toDouble(),
                                    0.0,
                                    10.0,
                                    true
                                )
                            } else if (uniform.get() is TypeFloat) {
                                uniformSetting = Setting(
                                    "${type.name} ${type1.name} ${shader.displayName} ${uniform.settingName}",
                                    module,
                                    (uniform.get().get() as Float).toDouble(),
                                    0.0,
                                    10.0,
                                    false
                                )
                            } else if (uniform.get() is TypeBool) {
                                uniformSetting = Setting(
                                    "${type.name} ${type1.name} ${shader.displayName} ${uniform.settingName}",
                                    module,
                                    uniform.get().get() as Boolean
                                )
                            } else {
                                continue
                            }

                            uniformSetting.title = uniform.settingName

                            shaderGroup.add(uniformSetting)

                            val uniformSCRSetting = ShaderCharmsRewriteSetting(
                                uniformSetting,
                                type,
                                type1,
                                shader,
                                ShaderCharmsRewriteSettingTypes.UniformOfShader,
                                uniform.index
                            )

                            settings.add(uniformSCRSetting)

                            uniformsMap[uniform.index] = uniformSCRSetting
                        }
                    }

                    uniforms[shader.index] = Bind(
                        shader,
                        uniformsMap
                    )

                    /*uniforms.add(Bind(
                        Bind(
                            shader.index,
                            shader
                        ),
                        uniformsMap
                    ))*/
                }
            }
        }

        needToRenderEntities = ThreaddedBooleanSupplier { needToProcessType(ShaderCharmsRewriteObjectTypes.Entity) }
        needToRenderTileEntities = ThreaddedBooleanSupplier { needToProcessType(ShaderCharmsRewriteObjectTypes.TileEntity) }
        needToRenderHand = ThreaddedBooleanSupplier { needToProcessType(ShaderCharmsRewriteObjectTypes.Hand) }
    }

    fun init() : ShaderCharmsRewritePattern {
        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            module.register(setting.setting)
        }

        module.register(optimizationGroup)
        module.register(listProcessing)

        module.register(itemsFix)

        return this
    }

    fun getSetting(
        type : ShaderCharmsRewriteObjectTypes,
        option : ShaderCharmsObjectsEnum.ShaderCharmsRewriteObjects,
        shader : ShaderCharmsRewriteShaders?,
        typeS : ShaderCharmsRewriteSettingTypes
    ) : ShaderCharmsRewriteSetting? {
        for(setting in settings) {
            if(
                setting.type == type
                && setting.option == option
                && setting.shader == shader
                && setting.typeS == typeS
            ) {
                return setting
            }
        }

        return null
    }

    fun getOptionSettingsByType(type : ShaderCharmsRewriteObjectTypes) : List<ShaderCharmsRewriteSetting> {
        val list = ArrayList<ShaderCharmsRewriteSetting>()

        for(option in ShaderCharmsObjectsEnum.ShaderCharmsRewriteObjects.byType(type)) {

        }

        return list
    }

    private fun needToProcessType(type : ShaderCharmsRewriteObjectTypes) : Boolean {
        for(setting in settings) {
            if(
                setting.type == type
                && setting.shader == null
                && setting.typeS == ShaderCharmsRewriteSettingTypes.StateOfOption
                && setting.setting.valBoolean
            ) {
                return true
            }
        }

        return false
    }

    fun update() {
//        threads
    }

    fun render(ticks : Float) {


        if(needToRenderEntities.get()) {

        }

        if(needToRenderTileEntities.get()) {

        }

        if(needToRenderHand.get()) {

        }
    }

    fun renderHand() : Boolean = needToRenderHand.get() && itemsFix.valBoolean && criticalSection

    fun getFramebuffer() {

    }

    private var latestProcessedEntity : Entity? = null
    private var latestProcessedTileEntity : TileEntity? = null

    fun renderEntityPre(
        entity : Entity
    ) {
        if(validEntities.contains(entity)) {
            latestProcessedEntity = entity

            //start frame buffer
        } else {
            latestProcessedEntity = null
        }
    }

    fun renderEntityPost(
        entity : Entity
    ) {
        if(latestProcessedEntity != null) {
            //stop frame buffer
        }
    }

    fun renderTileEntityPre(
        entity : TileEntity
    ) {
        if(validTileEntities.contains(entity)) {
            latestProcessedTileEntity = entity

            for(shader in validTileEntities[entity]!!) {
//                shader.frame
            }
        } else {
            latestProcessedTileEntity = null
        }
    }

    fun renderTIleEntityPost(
        entity : TileEntity
    ) {
        if(latestProcessedTileEntity != null) {
            //stop frame buffer
        }
    }

    fun renderEntitiesPre() {
        matrixMode(5889)
        pushMatrix()
        matrixMode(5888)
        pushMatrix()
    }

    fun renderEntitiesPost() {
        color(1f, 1f, 1f)
        matrixMode(5889)
        popMatrix()
        matrixMode(5888)
        popMatrix()
    }

    /*fun doListProcessing() {
        val needToRenderEntities = false
        val needToRenderTileEntities = false
        val needToRenderHand = false

        for(setting in settings) {

        }
    }*/
}