package com.kisman.cc.util.manager.file

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.features.module.BindType
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.IBindable
import com.kisman.cc.features.module.Module
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.fromColorConfig
import com.kisman.cc.util.toColorConfig
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author _kisman_, Cubic (only a few tweaks)
 * @since unknown
 */
class ConfigManager(
        val name : String
) {
    val saver = Save(this)
    val loader = Load(this)

    val moduleSaver = ModuleSave(this)

    val suffix = ".kis"
    val path = Kisman.fileName

    val modulesPrefix = "module"
    val settingsPrefix = "setting"
    val hudModulesPrefix = "hud_module"
    val hudEditorPrefix = "hud_editor"
    val friendsPrefix = "friend"

    class ModuleSave(
        val config : ConfigManager
    ) {
        @Throws(IOException::class)
        fun init(
            modules : ArrayList<Module>
        ) {
            Kisman.initDirs()

            fileCheck()

            BufferedWriter(
                FileWriter(
                    Paths.get(config.path + config.name + config.suffix).toFile()
                )
            ).use { writer ->
                save(writer, modules)
            }
        }

        @Throws(IOException::class)
        private fun save(writer : BufferedWriter, modules : ArrayList<Module>) {
            for(module in modules) {
                val prefix = if(module is HudModule) config.hudModulesPrefix else config.modulesPrefix

                writer.write("${prefix}.${module.name}.toggle=${module.isToggled}")
                writer.newLine()
                writer.write("${prefix}.${module.name}.hold=${module.hold}")
                writer.newLine()
                writer.write("${prefix}.${module.name}.visible=${module.isVisible}")
                writer.newLine()
                writer.write("${prefix}.${module.name}.key=${module.getKeyboardKey()}")
                writer.newLine()
                writer.write("${prefix}.${module.name}.button=${module.getMouseButton()}")
                writer.newLine()
                writer.write("${prefix}.${module.name}.mouseBind=${module.getType() == BindType.Mouse}")
                writer.newLine()

                if (module is HudModule) {
                    writer.write("${prefix}.${module.name}.x=${module.getX()}")
                    writer.newLine()
                    writer.write("${prefix}.${module.name}.y=${module.getY()}")
                    writer.newLine()
                }

                if (Kisman.instance.settingsManager.getSettingsByMod(module) != null) {
                    for (setting in Kisman.instance.settingsManager.getSettingsByMod(module)) {
                        if (setting != null && !setting.isGroup) {
                            if (setting.isCheck) {
                                writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valBoolean}")
                                writer.newLine()
                                if (setting.getKeyboardKey() != -1) {
                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}:key=${setting.getKeyboardKey()}")
                                    writer.newLine()
                                }
                                if (setting.getMouseButton() != -1) {
                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}:button=${setting.getKeyboardKey()}")
                                    writer.newLine()
                                }
                                if (IBindable.valid(setting)) {
                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if (setting.isCombo) {
                                writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}=\"${setting.valString}\"")
                                writer.newLine()

                                for (option in setting.binders.keys) {
                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:key=${setting.binders[option]!!.getKeyboardKey()}")
                                    writer.newLine()

                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:mouse=${setting.binders[option]!!.getMouseButton()}")
                                    writer.newLine()

                                    writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if (setting.isSlider) {
                                writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valDouble}")
                                writer.newLine()
                            }
                            if (setting.isColorPicker) {
                                writer.write("${prefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${toColorConfig(setting.colour)}")
                                writer.newLine()
                            }
                        }
                    }
                }
            }
        }

        @Throws(IOException::class)
        private fun fileCheck() {
            if(Files.exists(Paths.get(config.path + config.name + config.suffix))) {
                File(config.path + config.name + config.suffix).delete()
            } else {
                Files.createFile(Paths.get(config.path + config.name + config.suffix))
            }
        }
    }

    class Load (
            val config : ConfigManager
    ) {
        @Throws(IOException::class)
        fun init() {
            Kisman.currentConfig = config.name;

            Kisman.initDirs()

            if(!Files.exists(Paths.get(config.path + config.name + config.suffix))) {
                return
            }

            BufferedReader(InputStreamReader(Files.newInputStream(Paths.get(config.path + config.name + config.suffix)))).use { reader ->
                load(reader)
            }
        }

        @Throws(IOException::class)
        fun load(reader : BufferedReader) {
            var line : String?

            while(reader.readLine().also { line = it } != null) {
                if(line!!.startsWith("#")) {
                    continue
                }

                val split1 = line?.split("=")
                val split2 = split1?.get(0)?.split(".")

                when(split2?.get(0)) {
                    config.modulesPrefix -> {
                        val module = Kisman.instance.moduleManager.getModule(split2[1], false, false)
                        if(module != null) {
                            when(split2[2]) {
                                "toggle" -> {
                                    if(module.toggleable) {
                                        try {
                                            val toggle = java.lang.Boolean.parseBoolean(split1[1])
                                            if (module.isToggled != toggle) module.isToggled = toggle
                                        } catch (ignored: Exception) { }
                                    }
                                }
                                "hold" -> {
                                    try {
                                        module.hold = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "visible" -> {
                                    try {
                                        module.visible = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "key" -> {
                                    try {
                                        module.setKeyboardKey(Integer.parseInt(split1[1]))
                                    } catch(ignored : Exception) {}
                                }
                                "button" -> {
                                    try {
                                        module.setMouseButton(Integer.parseInt(split1[1]))
                                    } catch(ignored : Exception) {}
                                }
                                "mouseBind" -> {
                                    try {
                                        module.setType(if(java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                    } catch(ignored : Exception) {}
                                }
                                config.settingsPrefix -> {
                                    val flag1 = split2[3].contains(":")
                                    val flag2 = if(split2.size > 4) split2[4].contains(":") else false
                                    if(flag1 || flag2) {
                                        val setting = if(flag1) {
                                            Kisman.instance.settingsManager.getSettingByName(module, split2[3].split(":")[0], true)
                                        } else {//if(flag2) {
                                            Kisman.instance.settingsManager.getSettingByName(module, split2[3], true)
                                        }

                                        if(setting != null && !setting.isGroup) {
                                            val split3 = if(flag1) {
                                                split2[3].split(":")
                                            } else {//if(flag2) {
                                                split2[4].split(":")
                                            }

                                            if(setting.isCheck) {
                                                when (split3[1]) {
                                                    "key" -> {
                                                        try {
                                                            setting.setKeyboardKey(Integer.parseInt(split1[1]))
                                                        } catch (ignored : Exception) {}
                                                    }
                                                    "button" -> {
                                                        try {
                                                            setting.setMouseButton(Integer.parseInt(split1[1]))
                                                        } catch (ignored : Exception) {}
                                                    }
                                                    "mouseBind" -> {
                                                        try {
                                                            setting.setType(if(java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                                        } catch (ignored : Exception) {}
                                                    }
                                                }
                                            } else if(setting.isCombo) {
                                                val option = split3[0]

                                                if(setting.binders.containsKey(option)) {
                                                    when (split3[1]) {
                                                        "key" -> {
                                                            try {
                                                                setting.binders[option]?.setKeyboardKey(Integer.parseInt(split1[1]))
                                                            } catch (ignored : Exception) { }
                                                        }
                                                        "button" -> {
                                                            try {
                                                                setting.binders[option]?.setMouseButton(Integer.parseInt(split1[1]))
                                                            } catch (ignored : Exception) { }
                                                        }
                                                        "mouseBind" -> {
                                                            try {
                                                                setting.binders[option]?.setType(if (java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                                            } catch (ignored : Exception) { }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        val setting = Kisman.instance.settingsManager.getSettingByName(module, split2[3], true)

                                        if (setting != null && !setting.isGroup) {
                                            try {
                                                if (setting.isCheck) setting.valBoolean = java.lang.Boolean.parseBoolean(split1[1])
                                                if (setting.isCombo && setting.binders.containsKey(split1[1].split("\"")[1])) setting.valString = split1[1].split("\"")[1]
                                                if (setting.isSlider) setting.valDouble = java.lang.Double.parseDouble(split1[1])
                                                if (setting.isColorPicker) setting.colour = fromColorConfig(split1[1], setting.colour)
                                            } catch (_ : Exception) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                    config.hudModulesPrefix -> {
                        val hud = Kisman.instance.hudModuleManager.getModule(split2[1])
                        if(hud != null) {
                            when(split2[2]) {
                                "toggle" -> {
                                    try {
                                        val toggle = java.lang.Boolean.parseBoolean(split1[1])
                                        if(hud.isToggled != toggle) hud.isToggled = toggle
                                    } catch (ignored : Exception) {}
                                }
                                "hold" -> {
                                    try {
                                        hud.hold = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "visible" -> {
                                    try {
                                        hud.visible = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "key" -> {
                                    try {
                                        hud.setKeyboardKey(Integer.parseInt(split1[1]))
                                    } catch(ignored : Exception) {}
                                }
                                "button" -> {
                                    try {
                                        hud.setMouseButton(Integer.parseInt(split1[1]))
                                    } catch(ignored : Exception) {}
                                }
                                "mouseBind" -> {
                                    try {
                                        hud.setType(if(java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                    } catch(ignored : Exception) {}
                                }
                                "x" -> {
                                    try {
                                        hud.setX(java.lang.Double.parseDouble(split1[1]))
                                    } catch (ignored : Exception) {}
                                }
                                "y" -> {
                                    try {
                                        hud.setY(java.lang.Double.parseDouble(split1[1]))
                                    } catch (ignored : Exception) {}
                                }
                                config.settingsPrefix -> {
                                    val flag1 = split2[3].contains(":")
                                    val flag2 = if(split2.size > 4) split2[4].contains(":") else false
                                    if(flag1 || flag2) {
                                        val setting = if(flag1) {
                                            Kisman.instance.settingsManager.getSettingByName(hud, split2[3].split(":")[0], true)
                                        } else {//if(flag2) {
                                            Kisman.instance.settingsManager.getSettingByName(hud, split2[3], true)
                                        }

                                        if(setting != null && !setting.isGroup) {
                                            val split3 = if(flag1) {
                                                split2[3].split(":")
                                            } else {//if(flag2) {
                                                split2[4].split(":")
                                            }

                                            if(setting.isCheck) {
                                                when (split3[1]) {
                                                    "key" -> {
                                                        try {
                                                            setting.setKeyboardKey(Integer.parseInt(split1[1]))
                                                        } catch (ignored : Exception) {}
                                                    }
                                                    "button" -> {
                                                        try {
                                                            setting.setMouseButton(Integer.parseInt(split1[1]))
                                                        } catch (ignored : Exception) {}
                                                    }
                                                    "mouseBind" -> {
                                                        try {
                                                            setting.setType(if(java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                                        } catch (ignored : Exception) {}
                                                    }
                                                }
                                            } else if(setting.isCombo) {
                                                val option = split3[0]

                                                if(setting.binders.containsKey(option)) {
                                                    when (split3[1]) {
                                                        "key" -> {
                                                            try {
                                                                setting.binders[option]?.setKeyboardKey(Integer.parseInt(split1[1]))
                                                            } catch (ignored : Exception) { }
                                                        }
                                                        "button" -> {
                                                            try {
                                                                setting.binders[option]?.setMouseButton(Integer.parseInt(split1[1]))
                                                            } catch (ignored : Exception) { }
                                                        }
                                                        "mouseBind" -> {
                                                            try {
                                                                setting.binders[option]?.setType(if (java.lang.Boolean.parseBoolean(split1[1])) BindType.Mouse else BindType.Keyboard)
                                                            } catch (ignored : Exception) { }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        val setting = Kisman.instance.settingsManager.getSettingByName(hud, split2[3], true)

                                        if (setting != null && !setting.isGroup) {
                                            try {
                                                if (setting.isCheck) setting.valBoolean = java.lang.Boolean.parseBoolean(split1[1])
                                                if (setting.isCombo) setting.valString = split1[1].split("\"")[1]
                                                if (setting.isSlider) setting.valDouble = java.lang.Double.parseDouble(split1[1])
                                                if (setting.isColorPicker) setting.colour = fromColorConfig(split1[1], setting.colour)
                                            } catch (ignored : Exception) { }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    config.hudEditorPrefix -> {
                        when(split2[1]) {
                            "color" -> {
                                Kisman.instance.halqHudGui.settingsFrame.colorSetting.colour = fromColorConfig(split1[1], Kisman.instance.halqHudGui.settingsFrame.colorSetting.colour)
                            }
                        }
                    }
                    config.friendsPrefix -> {
                        FriendManager.instance.addFriend(split1[1].split("\"")[1])
                    }
                }
            }
        }
    }

    class Save(
            val config : ConfigManager
    ) {
        @Throws(IOException::class)
        fun init() {
            Kisman.initDirs()

            fileCheck()

            BufferedWriter(
                    FileWriter(
                            Paths.get(config.path + config.name + config.suffix).toFile()
                    )
            ).use { writer ->
                save(writer)
            }
        }

        @Throws(IOException::class)
        private fun save(writer : BufferedWriter) {
            for(module in Kisman.instance.moduleManager.modules) {
                if(module.category == Category.LUA) continue

                writer.write("${config.modulesPrefix}.${module.name}.toggle=${module.isToggled}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.hold=${module.hold}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.visible=${module.isVisible}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.key=${module.getKeyboardKey()}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.button=${module.getMouseButton()}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.mouseBind=${module.getType() == BindType.Mouse}")
                writer.newLine()
                if(Kisman.instance.settingsManager.getSettingsByMod(module) != null) {
                    for(setting in Kisman.instance.settingsManager.getSettingsByMod(module)) {
                        if(setting != null && !setting.isGroup) {
                            if(setting.isCheck) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valBoolean}")
                                writer.newLine()
                                if(setting.getKeyboardKey() != -1) {
                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}:key=${setting.getKeyboardKey()}")
                                    writer.newLine()
                                }
                                if(setting.getMouseButton() != -1) {
                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}:button=${setting.getKeyboardKey()}")
                                    writer.newLine()
                                }
                                if(IBindable.valid(setting)) {
                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if(setting.isCombo) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=\"${setting.valString}\"")
                                writer.newLine()

                                for(option in setting.binders.keys) {
                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:key=${setting.binders[option]!!.getKeyboardKey()}")
                                    writer.newLine()

                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:mouse=${setting.binders[option]!!.getMouseButton()}")
                                    writer.newLine()

                                    writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}.$option:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if(setting.isSlider) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valDouble}")
                                writer.newLine()
                            }
                            if(setting.isColorPicker) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${toColorConfig(setting.colour)}")
                                writer.newLine()
                            }
                        }
                    }
                }
            }
            for(hud in Kisman.instance.hudModuleManager.modules) {
                writer.write("${config.hudModulesPrefix}.${hud.name}.toggle=${hud.isToggled}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.hold=${hud.hold}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.visible=${hud.isVisible}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.key=${hud.key}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.key=${hud.getKeyboardKey()}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.button=${hud.getMouseButton()}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.mouseBind=${hud.getType() == BindType.Mouse}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.x=${hud.getX()}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.y=${hud.getY()}")
                writer.newLine()
                if(Kisman.instance.settingsManager.getSettingsByMod(hud) != null) {
                    for(setting in Kisman.instance.settingsManager.getSettingsByMod(hud)) {
                        if(setting != null && !setting.isGroup) {
                            if(setting.isCheck) {
                                writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}=${setting.valBoolean}")
                                writer.newLine()
                                if(setting.getKeyboardKey() != -1) {
                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}:key=${setting.getKeyboardKey()}")
                                    writer.newLine()
                                }
                                if(setting.getMouseButton() != -1) {
                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}:button=${setting.getMouseButton()}")
                                    writer.newLine()
                                }
                                if(IBindable.valid(setting)) {
                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if(setting.isCombo) {
                                writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}=\"${setting.valString}\"")
                                writer.newLine()

                                for(option in setting.binders.keys) {
                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}.$option:key=${setting.binders[option]!!.getKeyboardKey()}")
                                    writer.newLine()

                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}.$option:mouse=${setting.binders[option]!!.getMouseButton()}")
                                    writer.newLine()

                                    writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}.$option:mouseBind=${setting.getType() == BindType.Mouse}")
                                    writer.newLine()
                                }
                            }
                            if(setting.isSlider) {
                                writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}=${setting.valDouble}")
                                writer.newLine()
                            }
                            if(setting.isColorPicker) {
                                writer.write("${config.hudModulesPrefix}.${hud.name}.${config.settingsPrefix}.${setting.name}=${toColorConfig(setting.colour)}")
                                writer.newLine()
                            }
                        }
                    }
                }
            }

            writer.write("${config.hudEditorPrefix}.color=${toColorConfig(Kisman.instance.halqHudGui.color)}")
            writer.newLine()

            if(FriendManager.instance.friends.isNotEmpty()) {
                for(friend in FriendManager.instance.friends) {
                    writer.write("${config.friendsPrefix}=\"$friend\"")
                    writer.newLine()
                }
            }
        }

        @Throws(IOException::class)
        private fun fileCheck() {
            if(Files.exists(Paths.get(config.path + config.name + config.suffix))) {
                File(config.path + config.name + config.suffix).delete()
            } else {
                Files.createFile(Paths.get(config.path + config.name + config.suffix))
            }
        }
    }
}