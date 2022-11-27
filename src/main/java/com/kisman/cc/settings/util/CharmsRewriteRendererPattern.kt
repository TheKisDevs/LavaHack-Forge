package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.CharmsRewriteSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.clone
import com.kisman.cc.util.enums.CharmsRewriteModes
import com.kisman.cc.util.enums.CharmsRewriteEntityTypes
import com.kisman.cc.util.enums.CharmsRewriteTypeModes
import com.kisman.cc.util.enums.CharmsRewriteTypes
import com.kisman.cc.util.enums.dynamic.CharmsRewriteOptionsEnum
import net.minecraft.client.model.ModelBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11.*

/**
 * @author _kisman_
 * @since 17:20 of 11.07.2022
 */
class CharmsRewriteRendererPattern(
    val module : Module
) {
    private val groups = ArrayList<SettingGroup>()
    private val settings = ArrayList<CharmsRewriteSetting>()

    init {
        for(type in CharmsRewriteEntityTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))
            val wireGroup = group.add(SettingGroup(Setting("Wire", module)))
            val modelGroup = group.add(SettingGroup(Setting("Model", module)))
            val wireColorGroup = wireGroup.add(SettingGroup(Setting("Color", module)))
            val modelColorGroup = modelGroup.add(SettingGroup(Setting("Color", module)))
            val wireCrowdAlphaGroup = wireGroup.add(SettingGroup(Setting("Crowd", module)))
            val modelCrowdAlphaGroup = modelGroup.add(SettingGroup(Setting("Crowd", module)))
            groups.add(group)
            groups.add(wireGroup)
            groups.add(modelGroup)
            settings.addAll(getSettingsByType(
                type,
                group,
                wireGroup,
                modelGroup,
                wireColorGroup,
                modelColorGroup,
                wireCrowdAlphaGroup,
                modelCrowdAlphaGroup
            ))
        }
    }

    fun init() : CharmsRewriteRendererPattern {
        for(setting in settings) {
            module.register(setting.setting)
        }

        for(group in groups) {
            module.register(group)
        }

        return this
    }

    private fun getSettingsByType(
        type : CharmsRewriteEntityTypes,
        group : SettingGroup,
        wireGroup : SettingGroup,
        modelGroup : SettingGroup,
        wireColorGroup : SettingGroup,
        modelColorGroup : SettingGroup,
        wireCrowdAlphaGroup : SettingGroup,
        modelCrowdAlphaGroup : SettingGroup
    ) : ArrayList<CharmsRewriteSetting> {
        val list = ArrayList<CharmsRewriteSetting>()
        val mode = group.add(Setting("${type.name} Mode", module, CharmsRewriteModes.None).setTitle("Mode"))

        list.add(CharmsRewriteSetting(mode, type, CharmsRewriteTypes.Mode))
        list.add(CharmsRewriteSetting(group.add(Setting("${type.name} Width", module, 1.0, 0.1, 5.0, false).setTitle("Width")), type, CharmsRewriteTypes.Width))

        for(option in CharmsRewriteOptionsEnum.CharmsRewriteOptions.values()) {
            list.add(CharmsRewriteSetting(
                wireGroup.add(Setting("${type.name} Wire ${option.name}", module, false).setTitle(option.name)),
                type,
                option.typeW,
                option
            ))
        }

        list.add(CharmsRewriteSetting(
            wireCrowdAlphaGroup.add(Setting("${type.name} Wire Crowd Alpha", module, 1.0, 0.1, 1.0, false).setTitle("Alpha")),
            type,
            CharmsRewriteTypes.WireCrowdAlpha
        ))

        list.add(CharmsRewriteSetting(
            wireCrowdAlphaGroup.add(Setting("${type.name} Wire Crowd Alpha Range", module, 10.0, 1.0, 20.0, false).setTitle("Range")),
            type,
            CharmsRewriteTypes.WireCrowdAlphaRange
        ))

        list.add(CharmsRewriteSetting(
            wireColorGroup.add(Setting("${type.name} Wire Custom Color", module, false).setTitle("Custom")),
            type,
            CharmsRewriteTypes.WireCustomColor
        ))

        list.add(CharmsRewriteSetting(
            wireColorGroup.add(Setting("${type.name} Wire Color", module, Colour(255, 255, 255, 255)).setTitle("Custom")),
            type,
            CharmsRewriteTypes.WireColor
        ))

        for(option in CharmsRewriteOptionsEnum.CharmsRewriteOptions.values()) {
            list.add(CharmsRewriteSetting(
                modelGroup.add(Setting("${type.name} Model ${option.name}", module, false).setTitle(option.name)),
                type,
                option.typeM,
                option
            ))
        }

        list.add(CharmsRewriteSetting(
            modelCrowdAlphaGroup.add(Setting("${type.name} Model Crowd Alpha", module, 1.0, 0.1, 1.0, false).setTitle("Crowd Alpha")),
            type,
            CharmsRewriteTypes.ModelCrowdAlpha
        ))

        list.add(CharmsRewriteSetting(
            modelCrowdAlphaGroup.add(Setting("${type.name} Model Crowd Alpha Range", module, 10.0, 1.0, 20.0, false).setTitle("Range")),
            type,
            CharmsRewriteTypes.ModelCrowdAlphaRange
        ))

        list.add(CharmsRewriteSetting(
            modelColorGroup.add(Setting("${type.name} Model Custom Color", module, false).setTitle("Custom")),
            type,
            CharmsRewriteTypes.ModelCustomColor
        ))

        list.add(CharmsRewriteSetting(
            modelColorGroup.add(Setting("${type.name} Model Color", module, Colour(255, 255, 255, 255)).setTitle("Custom")),
            type,
            CharmsRewriteTypes.ModelColor
        ))

        list.add(CharmsRewriteSetting(
            group.add(Setting("${type.name} No Hurt", module, false).setTitle("No Hurt")),
            type,
            CharmsRewriteTypes.NoHurt
        ))

        return list
    }

    private fun getSettingByType(type : CharmsRewriteTypes, entity : Entity) : Setting? {
        for(setting in settings) {
            if(setting.typeE == CharmsRewriteEntityTypes.get(entity) && setting.typeS == type) {
                return setting.setting
            }
        }

        return null
    }

    /**
     * For players, monsters, animals, crystals
     */
    fun doRender(
        entity : Entity,
        model : ModelBase,
        limbSwing : Float,
        limbSwingAmount: Float,
        ageInTicks : Float,
        netHeadYaw : Float,
        headPitch : Float,
        scale : Float
    ) {
        if(CharmsRewriteEntityTypes.get(entity) == null || getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.None) {
            model.render(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale
            )

            return
        }

        if(entity is EntityLivingBase && getSettingByType(CharmsRewriteTypes.NoHurt, entity)?.valBoolean == true) {
            entity.hurtTime = 0
        }

        glPushMatrix()
        glPushAttrib(GL_ALL_ATTRIB_BITS)

        var step1Mode = CharmsRewriteTypeModes.Model

        if(getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.Wire) {
            step1Mode = CharmsRewriteTypeModes.Wire
        }

        doOptions(entity, false, step1Mode, false)

        if(step1Mode == CharmsRewriteTypeModes.Wire) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        }

        glEnable(GL_LINE_SMOOTH)
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        glLineWidth(getSettingByType(CharmsRewriteTypes.Width, entity)?.valFloat!!)

        model.render(
            entity,
            limbSwing,
            limbSwingAmount,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale
        )

        doOptions(entity, true, step1Mode, false)

        var step2Mode = CharmsRewriteTypeModes.Model

        if(getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.WireModel) {
            step2Mode = CharmsRewriteTypeModes.Wire
        }

        doOptions(entity, false, step2Mode, false)

        if(step2Mode == CharmsRewriteTypeModes.Wire) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        }

        model.render(
            entity,
            limbSwing,
            limbSwingAmount,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale
        )

        doOptions(entity, true, step2Mode, false)

        glPopAttrib()
        glPopMatrix()
    }

    private fun doOptions(
        entity : Entity,
        end : Boolean,
        mode : CharmsRewriteTypeModes,
        @Suppress("SameParameterValue") exclude : Boolean
    ) {
        for(setting in settings) {
            if(setting.option != null && (if(exclude) (setting.typeS.mode != mode) else (setting.typeS.mode == mode)) && setting.typeE == CharmsRewriteEntityTypes.get(entity)) {
                if(end) {
                    if(setting.setting.valBoolean) {
                        setting.option.afterIfTrue.doTask()
                    } else {
                        setting.option.afterIfFalse.doTask()
                    }
                } else {
                    if(setting.setting.valBoolean) {
                        setting.option.beginIfTrue.doTask()
                    } else {
                        setting.option.beginIfFalse.doTask()
                    }
                }
            }
        }

        if(!end) {
            if (getSettingByType((if (mode == CharmsRewriteTypeModes.Wire) CharmsRewriteTypes.WireCustomColor else CharmsRewriteTypes.ModelCustomColor), entity)?.valBoolean!!) {
                val alphaFactor = if(getSettingByType((if (mode == CharmsRewriteTypeModes.Wire) CharmsRewriteTypes.WireCrowdAlpha else CharmsRewriteTypes.ModelCrowdAlpha), entity)?.valDouble!! != 1.0) {
                    //distance to entity / max distance = the result will have 0 and 1 limits
                    mc.player.getDistanceSq(entity) / getSettingByType((if (mode == CharmsRewriteTypeModes.Wire) CharmsRewriteTypes.WireCrowdAlphaRange else CharmsRewriteTypes.ModelCrowdAlphaRange), entity)?.valDouble!!
                } else {
                    1.0
                }

                clone(
                    getSettingByType(
                        (if (mode == CharmsRewriteTypeModes.Wire) CharmsRewriteTypes.WireColor else CharmsRewriteTypes.ModelColor),
                        entity
                    )?.colour
                )?.also { it.alpha = (it.alpha * alphaFactor).toInt().coerceIn(0, 255) }?.glColor()
            }
        } else {
            glColor4f(1f, 1f, 1f, 1f)
        }
    }
}