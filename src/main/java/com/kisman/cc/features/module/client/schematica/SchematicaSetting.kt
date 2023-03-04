package com.kisman.cc.features.module.client.schematica

import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 10:58 of 04.03.2023
 */
@Suppress("UNCHECKED_CAST")
class SchematicaSetting<T>(
    lavahackSetting : Setting,
    schematicaSetting : (T) -> Unit
) {
    init {
        lavahackSetting.onChange {
            when(it.mode) {
                "Slider" -> schematicaSetting(it.valDouble as T)
                "Check" -> schematicaSetting(it.valBoolean as T)
            }
        }
    }
}