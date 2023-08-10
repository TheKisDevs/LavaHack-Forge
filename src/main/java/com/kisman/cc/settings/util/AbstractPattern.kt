package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 22:08 of 03.08.2022
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractPattern<T>(
    val module : Module
) {
    var visible = Supplier { true }
    var prefix : String? = null
    var group : SettingGroup? = null

    val _allSettings = ArrayList<Setting>()
    val _settings = ArrayList<Setting>()
    val _groups = ArrayList<SettingGroup>()

    open fun visible(visible : Supplier<Boolean>) : T {
        this.visible = visible

        for(setting in _settings) {
            setting.setVisible(visible)
        }

        return this as T
    }

    open fun group(group : SettingGroup) : AbstractPattern<T> {
        this.group = group
        return this
    }

    open fun prefix(prefix : String) : T {
        this.prefix = prefix

        for(setting in _settings) {
            setting.name = "$prefix ${setting.name}"
        }

        return this as T
    }

    protected fun <T : Enum<*>> setupEnum(setting : SettingEnum<T>) : SettingEnum<T> {
        return setupSetting(setting) as SettingEnum<T>
    }

    protected fun setupSetting(setting : Setting) : Setting {
        return setting.also { _settings.add(it) ; _allSettings.add(it) }
    }

    protected fun setupGroup(group : SettingGroup) : SettingGroup {
        return (group.setVisible(visible) as SettingGroup).also { _groups.add(it) ; _allSettings.add(it) }
    }

    protected fun <T> setupArray(array : SettingArray<T>) : SettingArray<T> {
        return setupSetting(array) as SettingArray<T>
    }

    protected fun setupList(
        list : SettingsList
    ) : SettingsList {
        for(setting in list.settings.values) {
            setupSetting(setting)
        }

        return list
    }

    abstract fun preInit() : AbstractPattern<T>
    abstract fun init() : T
}