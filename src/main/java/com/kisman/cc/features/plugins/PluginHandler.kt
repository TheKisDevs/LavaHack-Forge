package com.kisman.cc.features.plugins

import com.kisman.cc.Kisman
import com.kisman.cc.features.plugins.managers.PluginManager
import com.kisman.cc.features.plugins.utils.Environment
import net.minecraft.launchwrapper.Launch
import org.spongepowered.asm.mixin.Mixins

/**
 * @author _kisman_
 * @since 13:49 of 08.06.2022
 */
class PluginHandler {
    init {
        Environment.loadEnvironment()
        PluginManager.getInstance().createPluginConfigs(Launch.classLoader)
    }

    fun coreModInit() {
        for(config in PluginManager.getInstance().configs.values) {
            Mixins.addConfigurations(config.mixinConfig)
        }
    }

    fun init() {
        PluginManager.getInstance().instantiatePlugins()

        for(config in PluginManager.getInstance().configs.values) {
            ModulePlugin(config).also {
                Kisman.instance.moduleManager.modules.add(it)

                try {
                    it.plugin.init()
                } catch(_ : Throwable) { }

                it.load()
            }

        }
    }
}