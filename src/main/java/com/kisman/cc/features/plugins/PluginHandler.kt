package com.kisman.cc.features.plugins

import com.kisman.cc.Kisman
import com.kisman.cc.features.plugins.managers.PluginManager
import com.kisman.cc.features.plugins.utils.Environment
import org.spongepowered.asm.mixin.Mixins

/**
 * @author _kisman_
 * @since 13:49 of 08.06.2022
 */
class PluginHandler {
    init {
        Environment.loadEnvironment()
        PluginManager.getInstance().createPluginConfigs(PluginManager::class.java.classLoader)
    }

    fun coreModInit() {
        for(config in PluginManager.getInstance().configs.values) {
            Mixins.addConfigurations(config.mixinConfig)
        }
    }

    fun init() {
        PluginManager.getInstance().instantiatePlugins()

        for(config in PluginManager.getInstance().configs.values) {
            val module = ModulePlugin(config)
            Kisman.instance.moduleManager.modules.add(module)
            try {
                module.plugin.init()
            } catch(ignored : IncompatibleClassChangeError) {}
            module.load()
        }
    }
}