package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class ExampleModule extends Module {
    public ExampleModule() {
        super("ExampleModule", "example", Category.CLIENT);
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory", this, 1, "ExampleCategory"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCLine", this, Kisman.instance.settingsManager.getSettingByName("ExampleCategory"), 1, "ExampleCLine"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory1", this, 2, "ExampleCategory1"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCLine1", this, Kisman.instance.settingsManager.getSettingByName("ExampleCategory"), 2, "ExampleCLine1"));
    }
}
