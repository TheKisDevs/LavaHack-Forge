package com.kisman.cc.file;

import com.google.gson.*;
import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveConfig {
    public static final String fileName = "kisman.cc/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";

    public static void init() {
        try {
            saveConfig();
            saveModules();
            saveEnabledModules();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Kisman.LOGGER.info("Saved Config!");
    }
    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
        }
        if (!Files.exists(Paths.get(fileName + moduleName))) {
            Files.createDirectories(Paths.get(fileName + moduleName));
        }
        if (!Files.exists(Paths.get(fileName + mainName))) {
            Files.createDirectories(Paths.get(fileName + mainName));
        }
        if (!Files.exists(Paths.get(fileName + miscName))) {
            Files.createDirectories(Paths.get(fileName + miscName));
        }
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(fileName + location + name + ".json"))) {
            File file = new File(fileName + location + name + ".json");

            file.delete();

        } else {
            Files.createFile(Paths.get(fileName + location + name + ".json"));
        }

    }

    private static void saveModules() throws IOException {
        for (Module module : Kisman.instance.moduleManager.getModuleList()) {
            try {
                boolean setting;

                if(Kisman.instance.settingsManager.getSettingsByMod(module) != null) {
                    if (Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty()) {
                        setting = false;
                    } else {
                        setting = true;
                    }
                } else {
                    setting = false;
                }

                saveModuleDirect(module, setting);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveModuleDirect(Module module, boolean settings) throws IOException {
        registerFiles(moduleName, module.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + moduleName + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(module.getName()));

        if(settings) {
            if(!(Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty())) {
                for (Setting setting : Kisman.instance.settingsManager.getSettingsByMod(module)) {
                    if (setting != null) {
                        if (setting.isCheck()) {
                            settingObject.add(setting.getName(), new JsonPrimitive(setting.getValBoolean()));
                        }
                        if (setting.isCombo()) {
                            settingObject.add(setting.getName(), new JsonPrimitive(setting.getValString()));
                        }
                        if (setting.isSlider()) {
                            settingObject.add(setting.getName(), new JsonPrimitive(setting.getValDouble()));
                        }
                        if(setting.isColorPicker()) {
                            settingObject.add(setting.getName() + "H", new JsonPrimitive(setting.getColorPicker().getColorHSB()[0]));
                            settingObject.add(setting.getName() + "S", new JsonPrimitive(setting.getColorPicker().getColorHSB()[0]));
                            settingObject.add(setting.getName() + "B", new JsonPrimitive(setting.getColorPicker().getColorHSB()[0]));
                            settingObject.add(setting.getName() + "A", new JsonPrimitive(setting.getColorPicker().getColorHSB()[0]));
                            settingObject.add(setting.getName() + "RainBow", new JsonPrimitive(setting.getColorPicker().isRainbowState()));
                        }
                    }
                }
            }

            settingObject.add("key", new JsonPrimitive(Keyboard.getKeyName(module.getKey())));
        }



        moduleObject.add("Settings", settingObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnabledModules() throws IOException{
        registerFiles(mainName, "Toggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Toggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for(Module mod : Kisman.instance.moduleManager.modules) {
            enabledObject.add(mod.getName(), new JsonPrimitive(mod.isToggled()));
        }

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveModuleKeybinds() throws IOException {
        registerFiles(mainName, "Key");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Key" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject keyObject = new JsonObject();

        for(Module mod : Kisman.instance.moduleManager.modules) {
            keyObject.add(mod.getName(), new JsonPrimitive(Keyboard.getKeyName(mod.getKey())));
        }

        moduleObject.add("Modules", keyObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}
