package com.kisman.cc.settings.types

import com.kisman.cc.settings.Setting

class SettingGroup(
        setting : Setting
) : Setting(setting.name, setting.parentMod) {
    val settings : ArrayList<Setting> = ArrayList()

    init {
        this.mode = "Group"
        this.setVisible(setting.visibleSupplier)
    }

    fun add(setting : Setting) : Setting {
        setting.parent_ = this
        settings.add(setting)
        return setting
    }
}