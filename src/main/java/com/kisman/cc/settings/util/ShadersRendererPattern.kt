package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.ShadersSetting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.client.collections.Bind
import com.kisman.cc.util.enums.ShadersObjectTypes
import com.kisman.cc.util.enums.ShadersSettingTypes
import com.kisman.cc.util.enums.ShadersShaders
import com.kisman.cc.util.enums.dynamic.ShadersObjectsEnum
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
class ShadersRendererPattern(
    private val module : Module
) {
    private val groups = ArrayList<SettingGroup>()
    private val settings = ArrayList<ShadersSetting>()

    private val optimizationGroup = SettingGroup(Setting("Optimization", module))

    private val threads = MultiThreaddableModulePattern(module)

    private val itemsFix = Setting("Items Fix", module, false)

    private var needToRenderHand = ThreaddedBooleanSupplier { false }

    private var criticalSection = false

    private val shaderMap = HashMap<
            ShadersShaders,
            HashMap<
                    ShadersObjectsEnum.ShadersObjects,
                    ThreaddedBooleanSupplier
            >
    >()

    val uniforms = HashMap<
            Int,
            Bind<
                    ShadersShaders,
                    HashMap<
                            Int,
                            ShadersSetting
                            >
                    >
    >()

    private val validEntities = HashMap<
            Entity,
            ArrayList<ShadersShaders>
    >()


    private val validTileEntities = HashMap<
            TileEntity,
            ArrayList<ShadersShaders>
    >()

    init {
        for(type in ShadersObjectTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))

            groups.add(group)

            for(type1 in ShadersObjectsEnum.ShadersObjects.byType(type)) {
                val typeGroup = group.add(SettingGroup(Setting(type1.name, module)))
                val shadersGroup = typeGroup.add(SettingGroup(Setting("Shaders", module)))

                groups.add(typeGroup)
                groups.add(shadersGroup)

                settings.add(ShadersSetting(
                    typeGroup.add(Setting(
                        "$type $type1 State",
                        module,
                        false
                    ).setTitle("State")),
                    type,
                    type1,
                    null,
                    ShadersSettingTypes.StateOfOption,
                    -1
                ))

                for(shader in ShadersShaders.values()) {
                    settings.add(ShadersSetting(
                        shadersGroup.add(Setting(
                            "$type $type1 ${shader.displayName} State",
                            module,
                            false
                        ).setTitle(shader.displayName)),
                        type,
                        type1,
                        shader,
                        ShadersSettingTypes.ShaderStateOfOption,
                        -1
                    ))

                    val shaderGroup = typeGroup.add(SettingGroup(Setting(shader.displayName, module)))

                    val uniformsMap = HashMap<Int, ShadersSetting>()

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

                            val uniformSCRSetting = ShadersSetting(
                                uniformSetting,
                                type,
                                type1,
                                shader,
                                ShadersSettingTypes.UniformOfShader,
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
                }
            }
        }

        needToRenderHand = ThreaddedBooleanSupplier { needToProcessType(ShadersObjectTypes.Hand) }
    }

    fun init() : ShadersRendererPattern {
        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            module.register(setting.setting)
        }

        module.register(optimizationGroup)

        module.register(itemsFix)

        threads.init()

        return this
    }

    private fun needToProcessType(type : ShadersObjectTypes) : Boolean {
        for(setting in settings) {
            if(
                setting.type == type
                && setting.shader == null
                && setting.typeS == ShadersSettingTypes.StateOfOption
                && setting.setting.valBoolean
            ) {
                return true
            }
        }

        return false
    }

    fun update() {
        threads.update(Runnable {

        })
    }

    fun renderHand() : Boolean = needToRenderHand.get() && itemsFix.valBoolean && criticalSection

    private var latestProcessedEntity : Entity? = null
    private var latestProcessedTileEntity : TileEntity? = null

    fun renderEntityPre(
        entity : Entity
    ) {
        if(validEntities.contains(entity)) {
            latestProcessedEntity = entity

            for(shader in validEntities[entity]!!) {
                shader.shader.pattern(this).startDraw(mc.renderPartialTicks)
            }
        } else {
            latestProcessedEntity = null
        }
    }

    fun renderEntityPost(
        entity : Entity
    ) {
        if(latestProcessedEntity != null) {
            for(shader in validEntities[entity]!!) {
                shader.shader.pattern(this).stopDraw()
            }
        }
    }

    fun renderTileEntityPre(
        entity : TileEntity
    ) {
        if(validTileEntities.contains(entity)) {
            latestProcessedTileEntity = entity

            for(shader in validTileEntities[entity]!!) {
                shader.shader.pattern(this).startDraw(mc.renderPartialTicks)
            }
        } else {
            latestProcessedTileEntity = null
        }
    }

    fun renderTileEntityPost(
        entity : TileEntity
    ) {
        if(latestProcessedTileEntity != null) {
            for(shader in validTileEntities[entity]!!) {
                shader.shader.pattern(this).stopDraw()
            }
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
}