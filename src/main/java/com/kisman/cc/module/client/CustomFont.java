package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

import java.util.*;

public class CustomFont extends Module {
    public Setting mode = new Setting("Mode", this, "Comfortaa", new ArrayList<>(Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Consolas")));
    public Setting bold = new Setting("Bold", this, false);
    public Setting italic = new Setting("Italic", this, false);

    public static boolean turnOn = false;

    public static CustomFont instance;
    public CustomFont() {
        super("CustomFont", "custom font", Category.CLIENT);

        instance = this;

        setmgr.rSetting(mode);
        setmgr.rSetting(bold);
        setmgr.rSetting(italic);
    }

    public void update() {turnOn = true;}
    public void onDisable(){turnOn = false;}
}
