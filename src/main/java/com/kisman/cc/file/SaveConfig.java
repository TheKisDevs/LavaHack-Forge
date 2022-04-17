package com.kisman.cc.file;

import com.google.gson.*;
import com.kisman.cc.Kisman;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColourUtilKt;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class SaveConfig {
    public static void init() {
        try {
            Kisman.initDirs();
            saveModules();
            saveEnabledModules();
            saveVisibledModules();
            saveEnabledHudModules();
            saveBindModes();
            saveFriends();
        } catch (IOException ignored) {}
        Kisman.LOGGER.info("Saved Config!");
    }

    private static void saveFriends() throws IOException {
        if (Files.exists(Paths.get(Kisman.fileName + Kisman.miscName + "friends.txt"))) new File(Kisman.fileName + Kisman.miscName + "friends.txt").delete();
        else Files.createFile(Paths.get(Kisman.fileName + Kisman.miscName + "friends.txt"));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(Kisman.fileName + Kisman.miscName + "friends.txt").toFile()))) {
            for (int i = 0; i < FriendManager.instance.getFriends().size(); i++) {
                bw.write(FriendManager.instance.getFriends().get(i));
                if (i != FriendManager.instance.getFriends().size() - 1) bw.newLine();
            }
        }
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(Kisman.fileName + location + name + ".json"))) new File(Kisman.fileName + location + name + ".json").delete();
        else Files.createFile(Paths.get(Kisman.fileName + location + name + ".json"));
    }

    private static void saveModules() throws IOException {
        for (Module module : Kisman.instance.moduleManager.getModuleList()) try {saveModuleDirect(module, Kisman.instance.settingsManager.getSettingsByMod(module) != null && !Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty());} catch (IOException e) {e.printStackTrace();}
    }

    private static void saveModuleDirect(Module module, boolean settings) throws IOException {
        registerFiles(Kisman.moduleName, module.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.moduleName + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(module.getName()));

        if(settings) {
            if(!(Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty())) {
                for (Setting setting : Kisman.instance.settingsManager.getSettingsByMod(module)) {
                    if (setting != null) {
                        if (setting.isCheck()) settingObject.add(setting.getName(), new JsonPrimitive(setting.getValBoolean()));
                        if (setting.isCombo()) settingObject.add(setting.getName(), new JsonPrimitive(setting.getValString()));
                        if (setting.isSlider()) settingObject.add(setting.getName(), new JsonPrimitive(setting.getValDouble()));
                        if(setting.isColorPicker()) settingObject.add(setting.getName(), new JsonPrimitive(ColourUtilKt.Companion.toConfig(setting.getColour())));
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
        registerFiles(Kisman.mainName, "Toggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.mainName + "Toggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for(Module mod : Kisman.instance.moduleManager.modules) enabledObject.add(mod.getName(), new JsonPrimitive(mod.isToggled()));

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveVisibledModules() throws IOException {
        registerFiles(Kisman.mainName, "Visible");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.mainName + "Visible" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for(Module mod : Kisman.instance.moduleManager.modules) enabledObject.add(mod.getName(), new JsonPrimitive(mod.visible));

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnabledHudModules() throws IOException {
        registerFiles(Kisman.mainName, "HudToggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.mainName + "HudToggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for(HudModule mod : Kisman.instance.hudModuleManager.modules) enabledObject.add(mod.getName(), new JsonPrimitive(mod.isToggled()));

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveBindModes() throws IOException {
        registerFiles(Kisman.mainName, "BindModes");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.mainName + "BindModes" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for(Module mod : Kisman.instance.moduleManager.modules) enabledObject.add(mod.getName(), new JsonPrimitive(mod.hold));

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveHud() throws IOException {
        for(HudModule module : Kisman.instance.hudModuleManager.modules) saveHudDirect(module);
    }

    private static void saveHudDirect(HudModule module) throws IOException {
        registerFiles(Kisman.hudName, module.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(Kisman.fileName + Kisman.hudName + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject posObject = new JsonObject();

        posObject.add("x", new JsonPrimitive(module.getX()));
        posObject.add("y", new JsonPrimitive(module.getY()));

        moduleObject.add("Pos", posObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}
