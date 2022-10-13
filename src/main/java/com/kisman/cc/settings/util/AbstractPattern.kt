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
    var visible : Supplier<Boolean> = (Supplier { true })
    var prefix : String? = null
    var group : SettingGroup? = null

    open fun visible(visible : Supplier<Boolean>) : T {
        this.visible = visible
        return this as T
    }

    open fun group(group : SettingGroup) : T {
        this.group = group
        return this as T
    }

    open fun prefix(prefix : String) : T {
        this.prefix = prefix
        return this as T
    }

    protected fun setupEnum(setting : SettingEnum<*>) : SettingEnum<*> {
        return setupSetting(setting) as SettingEnum<*>
    }

    protected fun setupSetting(setting : Setting) : Setting {
        return setting.setVisible(visible).setName((if(prefix != null) "$prefix " else "" ) + setting.name)
    }

    protected fun setupGroup(group : SettingGroup) : SettingGroup {
        return setupSetting(group) as SettingGroup
    }

    protected fun <T> setupArray(array : SettingArray<T>) : SettingArray<T> {
        return setupSetting(array) as SettingArray<T>
    }

    abstract fun preInit() : T
    abstract fun init() : T
}