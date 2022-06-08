package com.kisman.cc.features.plugins

import com.kisman.cc.features.plugins.managers.PluginManager
import com.kisman.cc.features.plugins.utils.Environment

/**
 * @author _kisman_
 * @since 13:49 of 08.06.2022
 */
class PluginHandler {
    fun init() {
        Environment.loadEnvironment()
        PluginManager.getInstance().createPluginConfigs(PluginManager::class.java.classLoader)
        PluginManager.getInstance().instantiatePlugins()
        for(plugin in PluginManager.getInstance().plugins.values) {
            plugin.load()
        }
    }
}