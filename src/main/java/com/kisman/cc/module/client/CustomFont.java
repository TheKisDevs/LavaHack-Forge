package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomFont extends Module {
    public Setting mode = new Setting("Mode", this, "Default", new ArrayList<>(Arrays.asList("Default", "SalHack")));

    public static boolean turnOn = false;

    public static CustomFont instance;
    public CustomFont() {
        super("CustomFont", "custom font", Category.CLIENT);

        instance = this;

        setmgr.rSetting(mode);
    }

    public void update() {
        if(mode.getValString().equalsIgnoreCase("Default")) {
            turnOn = true;
        }
    }

    public void onDisable(){
        turnOn = false;
    }
}
