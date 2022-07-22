package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.CharmsRewriteSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.CharmsRewriteModes
import com.kisman.cc.util.enums.CharmsRewriteEntityTypes
import com.kisman.cc.util.enums.CharmsRewriteTypeModes
import com.kisman.cc.util.enums.CharmsRewriteTypes
import com.kisman.cc.util.enums.dynamic.CharmsRewriteOptionsEnum
import net.minecraft.client.model.ModelBase
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
            val wireGroup = group.add(SettingGroup(Setting("${type.name} Wire", module).setTitle("Wire")))
            val modelGroup = group.add(SettingGroup(Setting("${type.name} Model", module).setTitle("Model")))
            groups.add(group)
            groups.add(wireGroup)
            groups.add(modelGroup)
            settings.addAll(getSettingsByType(type, group, wireGroup, modelGroup))
        }
    }

    fun init() : CharmsRewriteRendererPattern {
        for(setting in settings) {
            Kisman.instance.settingsManager.rSetting(setting.setting)
        }

        for(group in groups) {
            Kisman.instance.settingsManager.rSetting(group)
        }

        return this
    }

    private fun getSettingsByType(
        type : CharmsRewriteEntityTypes,
        group : SettingGroup,
        wireGroup : SettingGroup,
        modelGroup : SettingGroup
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
            wireGroup.add(Setting("${type.name} Wire Custom Color", module, false).setTitle("Custom Color")),
            type,
            CharmsRewriteTypes.WireCustomColor
        ))

        list.add(CharmsRewriteSetting(
            wireGroup.add(Setting("${type.name} Wire Color", module, Colour(255, 255, 255, 255)).setTitle("Color")),
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
            modelGroup.add(Setting("${type.name} Model Custom Color", module, false).setTitle("Custom Color")),
            type,
            CharmsRewriteTypes.ModelCustomColor
        ))

        list.add(CharmsRewriteSetting(
            modelGroup.add(Setting("${type.name} Model Color", module, Colour(255, 255, 255, 255)).setTitle("Color")),
            type,
            CharmsRewriteTypes.ModelColor
        ))

        return list
    }

    private fun getSettingByType(type : CharmsRewriteTypes, entity : EntityLivingBase) : Setting? {
        for(setting in settings) {
            if(setting.typeE == CharmsRewriteEntityTypes.get(entity) && setting.typeS == type) {
                return setting.setting
            }
        }

        return null
    }

    fun doRender(
        entity : EntityLivingBase,
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

    private fun doOptions(entity : EntityLivingBase, end : Boolean, mode : CharmsRewriteTypeModes, exclude : Boolean) {
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
                getSettingByType((if (mode == CharmsRewriteTypeModes.Wire) CharmsRewriteTypes.WireColor else CharmsRewriteTypes.ModelColor), entity)?.colour?.glColor()
            }
        } else {
            glColor4f(1f, 1f, 1f, 1f)
        }
    }
}