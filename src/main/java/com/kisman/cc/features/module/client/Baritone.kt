package com.kisman.cc.features.module.client

import baritone.api.BaritoneAPI
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.PingBypassModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType

@PingBypassModule
class Baritone : Module(
        "Baritone",
        "Config of baritone integration",
        Category.CLIENT
) {
//    private val default = register(SettingGroup(Setting("Default", this)))

    private val custom = register(SettingGroup(Setting("Custom", this)))

    init {
        /*for(entry in BaritoneAPI.getSettings().settingTypes.entries) {
            val clazzName = entry.value.typeName.split(" ")[1]
            val clazz = Class.forName(clazzName)

            when (clazz) {
                java.lang.Boolean::class.java -> {
                    register(default.add(Setting(entry.key.name, this, java.lang.Boolean.valueOf(entry.key.value))))
                }
            }
        }*/
    }
}