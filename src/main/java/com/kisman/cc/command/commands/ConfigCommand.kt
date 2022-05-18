package com.kisman.cc.command.commands

import com.kisman.cc.command.Command
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.manager.file.ConfigManager

/**
 * @author _kisman_
 * @since 16:56 of 18.05.2022
 */
class ConfigCommand : Command("config") {
    override fun runCommand(s: String?, args: Array<out String>?) {
        try {
            if((args?.get(0) ?: (throw Exception())) == "add") {
                //TODO
            } else if(args[0] == "save") {
                ConfigManager(args[1]).saver.init()
                complete("Config \"${args[1]}\" was saved!")
            } else if(args[0] == "load") {
                ConfigManager(args[1]).loader.init()
                complete("Config \"${args[1]}\" was loaded!")
            } else {
                throw Exception()
            }
        } catch (e : Exception) {
            error("Usage: $syntax")
        }
    }

    override fun getDescription(): String {
        return "cfg maker btw"
    }

    override fun getSyntax(): String {
        return "config create/save/load <name of config>"
    }
}