package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
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

    private val allSettings = ArrayList<Setting>()
    private val settings = ArrayList<Setting>()
    private val groups = ArrayList<SettingGroup>()

    open fun visible(visible : Supplier<Boolean>) : T {
        this.visible = visible

        for(setting in settings) {
            setting.setVisible(visible)
        }

        return this as T
    }

    open fun group(group : SettingGroup) : T {
        this.group = group
        return this as T
    }

    open fun prefix(prefix : String) : T {
        this.prefix = prefix

        for(setting in settings) {
            setting.name = "$prefix ${setting.name}"
        }

        return this as T
    }

    protected fun setupEnum(setting : SettingEnum<*>) : SettingEnum<*> {
        return setupSetting(setting) as SettingEnum<*>
    }

    protected fun setupSetting(setting : Setting) : Setting {
        return setting.also { settings.add(it) ; allSettings.add(it) }
    }

    protected fun setupGroup(group : SettingGroup) : SettingGroup {
        return (group.setVisible(visible) as SettingGroup).also { groups.add(it) ; allSettings.add(it) }
    }

    protected fun <T> setupArray(array : SettingArray<T>) : SettingArray<T> {
        return setupSetting(array) as SettingArray<T>
    }

    abstract fun preInit() : T
    abstract fun init() : T
}