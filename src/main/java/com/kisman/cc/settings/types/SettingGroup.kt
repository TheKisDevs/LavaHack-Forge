package com.kisman.cc.settings.types

import com.kisman.cc.settings.Setting

open class SettingGroup(
        setting : Setting
) : Setting(setting.name, setting.parentMod) {
    open val settings : ArrayList<Setting> = ArrayList()

    init {
        this.mode = "Group"
        this.setVisible(setting.visibleSupplier)
    }

    open fun add(setting : Setting) : Setting {
        setting.parent_ = this
        settings.add(setting)
        return setting
    }

    open fun add(group : SettingGroup) : SettingGroup {
        group.parent_ = this
        settings.add(group)
        return group
    }
}