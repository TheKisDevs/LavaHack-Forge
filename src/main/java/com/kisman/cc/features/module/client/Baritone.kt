package com.kisman.cc.features.module.client

import baritone.api.BaritoneAPI
import baritone.api.Settings
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.PingBypassModule
import com.kisman.cc.features.module.client.baritone.BaritoneSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType

@Suppress("UNCHECKED_CAST")
@PingBypassModule
class Baritone : Module(
        "Baritone",
        "Config of baritone integration",
        Category.CLIENT
) {
    private val settings = mutableListOf<BaritoneSetting<*>>()

    init {
        for(setting in BaritoneAPI.getSettings().allSettings) {
            if(setting.value == true || setting.value == false) {
                settings += BaritoneSetting(
                    register(Setting(setting.name, this, setting.value as Boolean)),
                    setting as Settings.Setting<Boolean>
                )
            }
        }
    }
}