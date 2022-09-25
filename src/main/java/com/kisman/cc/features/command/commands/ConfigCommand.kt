package com.kisman.cc.features.command.commands

import com.kisman.cc.Kisman
import com.kisman.cc.features.command.Command
import com.kisman.cc.features.module.Module
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
            if(args[0] == "save") {
                if(args.size != 2) {
                    val modules = ArrayList<Module>()

                    for(name in args[3].split(",")) {
                        if(args[2] == "module") {
                            if(Kisman.instance.moduleManager.getModule(name) != null) {
                                modules += Kisman.instance.moduleManager.getModule(name)
                            }
                        } else if(args[2] == "hud_module") {
                            if(Kisman.instance.hudModuleManager.getModule(name) != null) {
                                modules += Kisman.instance.hudModuleManager.getModule(name)
                            }
                        }
                    }

                    ConfigManager(args[1]).moduleSaver.init(modules)
                } else {
                    ConfigManager(args[1]).saver.init()
                    complete("Config \"${args[1]}\" was saved!")
                }
            } else if(args[0] == "load") {
                ConfigManager(args[1]).loader.init()
                complete("Config \"${args[1]}\" was loaded!")
            } else if(args[0] == "list") {
                val configs = ArrayList<String>()

                for (file in (File(Kisman.fileName).listFiles() ?: throw IllegalArgumentException("meow"))) {
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
                        config.split(".")[0]
                    }${
                        TextFormatting.GRAY
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
        return "config save/load <name>\nconfig list\nconfig save <name> module <module>\nconfig save <name> module <module1>,<module2>\nconfig save <name> hud_module <hud_module1>,<hud_module2>"
    }
}
