package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class CustomFont extends Module {
    public static boolean turnOn = false;
    public CustomFont() {
        super("CustomFont", "custom font", Category.CLIENT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {
        turnOn = true;
    }

    public void onDisable(){
        turnOn = false;
    }
}
