package com.kisman.cc.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadConfig {
    private static final String fileName = "kisman.cc/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";

    public static void init() {
        try {
            saveConfig();
            loadModules();
            loadEnabledModules();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void loadModules() throws IOException {
        String moduleLocation = fileName + moduleName;

        for (Module module : Kisman.instance.moduleManager.modules) {
            boolean settings;

            try {
                if(Kisman.instance.settingsManager.getSettingsByMod(module) == null) {
                    settings = false;
                } else if(Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty()) {
                    settings = false;
                } else {
                    settings = true;
                }
                loadModuleDirect(moduleLocation, module, settings);
            } catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    private static void loadModuleDirect(String moduleLocation, Module module, boolean settings)  throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        }catch (java.lang.IllegalStateException e) {
            return;
        }

        if (moduleObject.get("Module") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        JsonElement keyObject = settingObject.get("key");

        if(settings) {
            for (Setting setting : Kisman.instance.settingsManager.getSettingsByMod(module)) {
                JsonElement dataObject = settingObject.get(setting.getName());
                JsonElement[] colour = new JsonElement[] {
                        settingObject.get(setting.getName() + "H"),
                        settingObject.get(setting.getName() + "S"),
                        settingObject.get(setting.getName() + "B"),
                        settingObject.get(setting.getName() + "A"),
                        settingObject.get(setting.getName() + "RainBow"),
                        settingObject.get(setting.getName() + "Syns")
                };

                try {
                    if (dataObject != null && dataObject.isJsonPrimitive()) {
                        if (setting.isCheck()) {
                            setting.setValBoolean(dataObject.getAsBoolean());
                        }
                        if (setting.isCombo()) {
                            setting.setValString(dataObject.getAsString());
                        }
                        if (setting.isSlider()) {
                            setting.setValDouble(dataObject.getAsDouble());
                        }
                        if(setting.isColorPicker()) {
                            setting.getColorPicker().setColor(0, colour[0].getAsFloat());
                            setting.getColorPicker().setColor(1, colour[1].getAsFloat());
                            setting.getColorPicker().setColor(2, colour[2].getAsFloat());
                            setting.getColorPicker().setColor(3, colour[3].getAsFloat());
                            setting.setRainbow(colour[4].getAsBoolean());
                            setting.setSyns(colour[5].getAsBoolean());
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println(setting.getName() + " " + module.getName());
                    System.out.println(dataObject);
                }
            }
        }

        if(keyObject != null && keyObject.isJsonPrimitive()) {
            module.setKey(Keyboard.getKeyIndex(keyObject.getAsString()));
/*            try {
                module.setKey(Keyboard.getKeyIndex(keyObject.getAsString()));
            } catch (Exception e) {
                System.out.println("invalid key");
                System.out.println(keyObject);
            }*/
        }

        inputStream.close();
    }

    private static void loadEnabledModules() throws IOException{
        String enabledLocation = fileName + mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();

        for(Module module : Kisman.instance.moduleManager.modules) {
            JsonElement dataObject = settingObject.get(module.getName());

            if(dataObject != null && dataObject.isJsonPrimitive()) {
                try {
                    module.setToggled(dataObject.getAsBoolean());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        inputStream.close();
    }

    private static void loadModuleKeybinds() throws IOException {
        String keyLocation = fileName + mainName;

        if(!Files.exists(Paths.get(keyLocation + "Key" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(keyLocation + "Key" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if(moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();

        for(Module module : Kisman.instance.moduleManager.modules) {
            JsonElement dataObject = settingObject.get(module.getName());

            if(dataObject != null && dataObject.isJsonPrimitive()) {
                try {
                    module.setKey(Keyboard.getKeyIndex(dataObject.getAsString()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
