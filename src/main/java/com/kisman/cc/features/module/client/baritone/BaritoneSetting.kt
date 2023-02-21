package com.kisman.cc.features.module.client.baritone

import baritone.api.Settings
import com.kisman.cc.settings.Setting
import java.awt.Color

/**
 * @author _kisman_
 * @since 16:44 of 09.10.2022
 */
@Suppress("UNCHECKED_CAST")
class BaritoneSetting<T>(
    private val lavahackSetting : Setting,
    private val baritoneSetting : Settings.Setting<T>
) {
    init {
        lavahackSetting.onChange {
            when (baritoneSetting.value) {
                is Boolean -> baritoneSetting.value = lavahackSetting.valBoolean as T
//                is Float -> baritoneSetting.value = lavahackSetting.valFloat as T
//                is Double -> baritoneSetting.value = lavahackSetting.valDouble as T
//                is Int -> baritoneSetting.value = lavahackSetting.valInt as T
                is Color -> baritoneSetting.value = lavahackSetting.colour.color as T
            }
        }
    }
}