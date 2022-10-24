package com.kisman.cc.features.module.client

import baritone.api.BaritoneAPI
import baritone.api.Settings
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.PingBypassModule
import com.kisman.cc.features.module.client.baritone.BaritoneSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import java.awt.Color

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
            when (setting.value) {
                is Boolean -> {
                    settings += BaritoneSetting(
                        register(Setting(setting.name, this, setting.value as Boolean)),
                        setting as Settings.Setting<Boolean>
                    )
                }

                is Float, is Double, is Int -> {
                    val setting1 = when (setting.value) {
                        is Float -> setting as Settings.Setting<Float>
                        is Int -> setting as Settings.Setting<Int>
                        else -> setting as Settings.Setting<Double>
                    }
                    settings += BaritoneSetting(
                        register(Setting(setting.name, this, (
                                when(setting.value) {
                                    is Float -> (setting1.value as Float).toDouble()
                                    is Double -> (setting1.value as Double)
                                    else/*is Int*/ -> (setting1.value as Int).toDouble()
                                }
                                ), 0.0, 10.0, true)),
                        setting1
                    )
                }

                is Color -> {
                    settings += BaritoneSetting(
                        register(Setting(setting.name, this, Colour(setting.value as Color))),
                        setting as Settings.Setting<Color>
                    )
                }
            }
        }
    }
}