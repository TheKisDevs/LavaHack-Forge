package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.util.enums.PhaseModes

/**
 * @author _kisman_
 * @since 20:06 of 07.10.2022
 */
class Phase : Module(
    "Phase",
    "crystalpvp.cc go brr",
    Category.MOVEMENT
) {
    private val mode = SettingEnum("Mode", this, PhaseModes.Pearl).register()

    val autoDisable = register(Setting("Auto Disable", this, false))!!

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        mode.valEnum.update(this)
    }
}