package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class CustomFont extends Module {
    public static boolean turnOn = false;
    public CustomFont() {
        super("CustomFont", "custom font", Category.CLIENT);
    }

    public void update() {
        turnOn = true;
    }

    public void onDisable(){
        turnOn = false;
    }
}
