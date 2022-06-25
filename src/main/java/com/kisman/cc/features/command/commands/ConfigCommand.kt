package com.kisman.cc.features.command.commands

import com.kisman.cc.Kisman
import com.kisman.cc.features.command.Command
import com.kisman.cc.util.manager.file.ConfigManager
import net.minecraft.util.text.TextFormatting
import java.io.File

/**
 * @author _kisman_
 * @since 16:56 of 18.05.2022
 */
class ConfigCommand : Command("config") {
    override fun runCommand(s: String, args: Array<String>) {
        try {
            if((args?.get(0) ?: (throw Exception())) == "add") {
                //TODO
            } else if(args[0] == "save") {
                ConfigManager(args[1]).saver.init()
                complete("Config \"${args[1]}\" was saved!")
            } else if(args[0] == "load") {
                ConfigManager(args[1]).loader.init()
                complete("Config \"${args[1]}\" was loaded!")
            } else if(args[0] == "list") {
                val configs = ArrayList<String>()

                for(file in (File(Kisman.fileName + Kisman.pluginsName).listFiles() ?: throw IllegalArgumentException("meow"))) {
                    if(file.name.endsWith(".kis")) {
                        configs.add(file.name)
                    }
                }

                var output = "Configs: "

                for((i, config) in configs.withIndex()) {
                    output += "${
                        if(Kisman.currentConfig == config.split(".")[0]) {
                            TextFormatting.GREEN
                        } else {
                            TextFormatting.RED
                        }
                    }${
                        if(i != configs.size - 1) {
                            ", "
                        } else {
                            ""
                        }
                    }"
                }

                complete(output)
            } else {
                throw Exception()
            }
        } catch (e : Exception) {
            error("Usage: $syntax")
        } catch (e : IllegalArgumentException) {
            if(e.message == "meow") {
                error("StackTrace: ${e.stackTrace}")
            }
        }
    }

    override fun getDescription(): String {
        return "cfg maker btw"
    }

    override fun getSyntax(): String {
        return "config create/save <name> | config list"
    }
}