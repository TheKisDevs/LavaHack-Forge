package com.kisman.cc.settings

import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.AbstractPattern

/**
 * @author _kisman_
 * @since 16:05 of 12.03.2023
 */
@Suppress("UNCHECKED_CAST")
open class SettingsList(
    inputs : List<Any>
) {
    @JvmField val settings = mutableMapOf<String, Setting>()
    @JvmField val patterns = mutableMapOf<String, AbstractPattern<*>>()

    init {
        var key : String? = null

        for(setting in inputs) {
            when (setting) {
                is String -> key = setting
                is Setting -> settings[key!!] = setting
                is AbstractPattern<*> -> patterns[key!!] = setting
            }
        }
    }

    constructor(
        vararg inputs : Any
    ) : this(
        inputs.toList()
    )

    open fun <T : Setting> get0(
        name : String
    ) = this[name] as T

    fun <T : AbstractPattern<T>> pattern(
        name : String
    ) = patterns[name] as T

    open operator fun get(
        name : String
    ) : Setting = settings[name]!!

    class Groups(
        vararg groups : Any
    ) : SettingsList(
        groups.toList()
    ) {
        override operator fun get(
            name : String
        ) : SettingGroup = super.get(name) as SettingGroup
    }
}