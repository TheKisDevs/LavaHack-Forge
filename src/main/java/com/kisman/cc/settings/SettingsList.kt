package com.kisman.cc.settings

/**
 * @author _kisman_
 * @since 16:05 of 12.03.2023
 */
@Suppress("UNCHECKED_CAST")
class SettingsList(
    vararg settings0 : Any
) {
    @JvmField val settings = mutableMapOf<String, Setting>()

    init {
        var key : String? = null

        for(setting in settings0) {
            if(setting is String) {
                key = setting
            } else if(setting is Setting) {
                settings[key!!] = setting
            }
        }
    }

    fun <T : Setting> get0(
        name : String
    ) : T = this[name] as T

    operator fun get(
        name : String
    ) : Setting = settings[name]!!
}