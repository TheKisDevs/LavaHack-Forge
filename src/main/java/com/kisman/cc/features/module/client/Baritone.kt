package com.kisman.cc.features.module.client

import baritone.api.BaritoneAPI
import baritone.api.Settings
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.PingBypassModule
import com.kisman.cc.features.module.client.baritone.BaritoneSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import java.awt.Color

@Suppress("UNCHECKED_CAST")
@PingBypassModule
class Baritone : Module(
        "Baritone",
        "Config of baritone integration",
        Category.CLIENT
) {
    private val booleans = register(SettingGroup(Setting("Booleans", this)))
//    private val numbers = register(SettingGroup(Setting("Numbers", this)))
    private val colors = register(SettingGroup(Setting("Colors", this)))

    private val settings = mutableListOf<BaritoneSetting<*>>()

    init {
        toggled = true
        toggleable = false

        for(setting in BaritoneAPI.getSettings().allSettings) {
            when (setting.value) {
                is Boolean -> {
                    settings += BaritoneSetting(
                        register(booleans.add(Setting(setting.name, this, setting.value as Boolean))),
                        setting as Settings.Setting<Boolean>
                    )
                }

                /*is Float, is Double, is Int -> {
                    settings += BaritoneSetting(
                        register(numbers.add(Setting(setting.name, this, (
                                when(setting.value) {
                                    is Float -> (setting.value as Float).toDouble()
                                    is Double -> (setting.value as Double)
                                    else*//*is Int*//* -> (setting.value as Int).toDouble()
                                }
                                ), 0.0, 10.0, setting.value is Int))),
                        setting
                    )
                }*/

                is Color -> {
                    settings += BaritoneSetting(
                        register(colors.add(Setting(setting.name, this, Colour(setting.value as Color)))),
                        setting as Settings.Setting<Color>
                    )
                }
            }
        }
    }
}