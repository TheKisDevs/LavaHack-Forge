package com.kisman.cc.features.plugins

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.plugins.exceptions.BadPluginException
import com.kisman.cc.features.plugins.managers.PluginManager
import com.kisman.cc.util.chat.cubic.ChatUtility
import net.minecraft.client.Minecraft

/**
 * @author _kisman_
 * @since 19:52 of 09.06.2022
 */
class ModulePlugin(
    val config : PluginConfig
) : Module(config.name, "Plugin", Category.LUA) {
    val plugin : Plugin = PluginManager.getInstance().plugins[config] ?: throw BadPluginException("Cant get plugin by plugin config!!!!!")

    var loaded = true

    fun load() {
        plugin.load()
        if(Minecraft.getMinecraft().player != null) {
            ChatUtility.complete().printClientModuleMessage("Successful loaded ${config.name} plugin!")
        }
        loaded = true
        Kisman.reloadGUIs()
    }

    fun unload() {
        plugin.unload()
        if(Minecraft.getMinecraft().player != null) {
            ChatUtility.complete().printClientModuleMessage("Successful unloaded ${config.name} plugin!")
        }
        loaded = false
        Kisman.reloadGUIs()
    }

    fun reload() {
        plugin.unload()
        plugin.load()
        if(Minecraft.getMinecraft().player != null) {
            ChatUtility.complete().printClientModuleMessage("Successful reloaded ${config.name} plugin!")
        }
    }
}