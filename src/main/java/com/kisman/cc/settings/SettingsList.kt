package com.kisman.cc.settings

import com.kisman.cc.settings.util.AbstractPattern

/**
 * @author _kisman_
 * @since 16:05 of 12.03.2023
 */
@Suppress("UNCHECKED_CAST")
class SettingsList(
    vararg settings0 : Any
) {
    @JvmField val settings = mutableMapOf<String, Setting>()
    @JvmField val patterns = mutableMapOf<String, AbstractPattern<*>>()

    init {
        var key : String? = null

        for(setting in settings0) {
            when (setting) {
                is String -> key = setting
                is Setting -> settings[key!!] = setting
                is AbstractPattern<*> -> patterns[key!!] = setting
            }
        }
    }

    fun <T : Setting> get0(
        name : String
    ) = this[name] as T

    fun <T : AbstractPattern<T>> pattern(
        name : String
    ) = patterns[name] as T

    operator fun get(
        name : String
    ) : Setting = settings[name]!!
}