package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.CharmsRewriteSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.CharmsRewriteModes
import com.kisman.cc.util.enums.CharmsRewriteEntityTypes
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
    val groups = ArrayList<SettingGroup>()
    val settings = ArrayList<CharmsRewriteSetting>()

    init {
        for(type in CharmsRewriteEntityTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))
            groups.add(group)
            settings.addAll(getSettingsByType(type, group))
        }
    }

    fun init() : CharmsRewriteRendererPattern {
        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            module.register(setting.setting)
        }

        return this
    }

    private fun getSettingsByType(type : CharmsRewriteEntityTypes, group : SettingGroup) : ArrayList<CharmsRewriteSetting> {
        val list = ArrayList<CharmsRewriteSetting>()
        val mode = group.add(Setting("${type.name} Mode", module, CharmsRewriteModes.None))

        list.add(CharmsRewriteSetting(mode, type, CharmsRewriteTypes.Mode))
        list.add(CharmsRewriteSetting(group.add(Setting("${type.name} Width", module, 1.0, 0.1, 5.0, false)), type, CharmsRewriteTypes.Width))

        for(option in CharmsRewriteOptionsEnum.CharmsRewriteOptions.values()) {
            list.add(CharmsRewriteSetting(
                group.add(Setting("${type.name} ${option.name}", module, false)),
                type,
                option.type,
                option
            ))
        }

        list.add(CharmsRewriteSetting(
            group.add(Setting("${type.name} Custom Color", module, false)),
            type,
            CharmsRewriteTypes.CustomColor
        ))

        list.add(CharmsRewriteSetting(
            group.add(Setting("${type.name} Color", module, Colour(255, 255, 255, 255))),
            type,
            CharmsRewriteTypes.Color
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
        if(CharmsRewriteEntityTypes.get(entity) == null) return

        if(getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.None) {
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

        for(setting in settings) {
            if(setting.option != null && setting.typeE == CharmsRewriteEntityTypes.get(entity)) {
                if(setting.setting.valBoolean) {
                    setting.option.beginIfTrue.doTask()
                } else {
                    setting.option.beginIfFalse.doTask()
                }
            }
        }

        if(getSettingByType(CharmsRewriteTypes.CustomColor, entity)?.valBoolean!!) {
            getSettingByType(CharmsRewriteTypes.Color, entity)?.colour?.glColor()
        }

        if(getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.Wire) {
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

        if(getSettingByType(CharmsRewriteTypes.Mode, entity)?.valEnum == CharmsRewriteModes.WireModel) {
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

        for(setting in settings) {
            if(setting.option != null && setting.typeE == CharmsRewriteEntityTypes.get(entity)) {
                if(setting.setting.valBoolean) {
                    setting.option.afterIfTrue.doTask()
                } else {
                    setting.option.afterIfFalse.doTask()
                }
            }
        }

        glPopAttrib()
        glPopMatrix()
    }
}