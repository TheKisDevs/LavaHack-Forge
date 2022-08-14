package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType

/**
 * @author _kisman_
 * @since 22:07 of 03.08.2022
 */
class MovableRendererPattern(
    module : Module
) : AbstractPattern<MovableRendererPattern>(
    module
) {
    private val lengthsGroup = setupGroup(SettingGroup(Setting("Lengths", module)))

    @JvmField val movingLength = setupSetting(lengthsGroup.add(Setting("Moving Length", module, 400.0, 0.0, 1000.0, NumberType.TIME).setTitle("Moving")))
    @JvmField val fadeLength = setupSetting(lengthsGroup.add(Setting("Fade Length", module, 200.0, 0.0, 1000.0, NumberType.TIME).setTitle("Fade")))

    override fun preInit(): MovableRendererPattern {
        if(group != null) {
            group?.add(lengthsGroup)
        }

        return this
    }

    override fun init(): MovableRendererPattern {
        module.register(lengthsGroup)
        module.register(movingLength)
        module.register(fadeLength)

        return this
    }
}