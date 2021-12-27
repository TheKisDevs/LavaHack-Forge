package com.kisman.cc.file;

import com.google.gson.*;
import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudModule;
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
    public static void init() {
        try {
            Kisman.initDirs();
            loadModules();
            loadEnabledModules();
            loadVisibledModules();
            loadEnabledHudModules();
        } catch (IOException e) {e.printStackTrace();}
    }

    private static void loadModules() throws IOException {
        for (Module module : Kisman.instance.moduleManager.modules) {
            boolean settings;

            try {
                if(Kisman.instance.settingsManager.getSettingsByMod(module) == null) settings = false;
                else settings = !Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty();
                loadModuleDirect(Kisman.fileName + Kisman.moduleName, module, settings);
            } catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    private static void loadModuleDirect(String moduleLocation, Module module, boolean settings)  throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))) return;

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject;

        try {moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();} catch (java.lang.IllegalStateException e) {return;}

        if (moduleObject.get("Module") == null) return;

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
                        if (setting.isCheck()) setting.setValBoolean(dataObject.getAsBoolean());
                        if (setting.isCombo()) setting.setValString(dataObject.getAsString());
                        if (setting.isSlider()) setting.setValDouble(dataObject.getAsDouble());
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

        if(keyObject != null && keyObject.isJsonPrimitive()) module.setKey(Keyboard.getKeyIndex(keyObject.getAsString()));

        inputStream.close();
    }

    private static void loadEnabledModules() throws IOException{
        String enabledLocation = Kisman.fileName + Kisman.mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))) return;

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) return;

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();

        for(Module module : Kisman.instance.moduleManager.modules) {
            JsonElement dataObject = settingObject.get(module.getName());

            if(dataObject != null && dataObject.isJsonPrimitive()) try {module.setToggled(dataObject.getAsBoolean());} catch (NullPointerException e) {e.printStackTrace();}
        }

        inputStream.close();
    }

    private static void loadVisibledModules() throws IOException {
        String enabledLocation = Kisman.fileName + Kisman.mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Visible" + ".json"))) return;

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Visible" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) return;

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();

        for(Module module : Kisman.instance.moduleManager.modules) {
            JsonElement dataObject = settingObject.get(module.getName());

            if(dataObject != null && dataObject.isJsonPrimitive()) try {module.visible = dataObject.getAsBoolean();} catch (NullPointerException e) {e.printStackTrace();}

        }

        inputStream.close();
    }

    private static void  loadEnabledHudModules() throws IOException {
        String enabledLocation = Kisman.fileName + Kisman.mainName;

        if (!Files.exists(Paths.get(enabledLocation + "HudToggle" + ".json"))) return;

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "HudToggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) return;

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();

        for(HudModule module : Kisman.instance.hudModuleManager.modules) {
            JsonElement dataObject = settingObject.get(module.getName());

            if(dataObject != null && dataObject.isJsonPrimitive()) try {module.setToggled(dataObject.getAsBoolean());} catch (NullPointerException e) {e.printStackTrace();}
        }

        inputStream.close();
    }
}
