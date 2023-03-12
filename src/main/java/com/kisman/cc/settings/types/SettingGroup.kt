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

    open fun <T : Enum<*>> add(enum : SettingEnum<T>) : SettingEnum<T> {
        enum.parent_ = this
        settings.add(enum)
        return enum
    }

    open fun <T> add(array : SettingArray<T>) : SettingArray<T> {
        array.parent_ = this
        settings.add(array)
        return array
    }

    open fun <S1 : Setting, S2 : Setting> add(
        pair : SettingPair<S1, S2>
    ) : SettingPair<S1, S2> = pair.also {
        add(it.first)
        add(it.second)
    }

    fun remove(
        setting : Setting
    ) {
        setting.parent_ = null
        settings.remove(setting)
    }
}