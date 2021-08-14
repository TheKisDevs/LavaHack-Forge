package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class SettingUtil {
    public static Module parent;

    public static void ColorSetting(Module parent, String name) {
        SettingUtil.parent = parent;
        Kisman.instance.settingsManager.rSetting(new Setting(name + "R", parent, 255, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting(name + "G", parent, 255, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting(name + "B", parent, 255, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting(name + "A", parent, 255, 0, 255, true));
    }
}
