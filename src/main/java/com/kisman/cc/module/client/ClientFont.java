package com.kisman.cc.module.client;

import com.kisman.cc.module.*;

public class ClientFont extends Module {
    public static ClientFont instance;

    public static boolean turnOn = false;

    public ClientFont() {
        super("ClientFont", "yes i do", Category.CLIENT);

        instance = this;
    }

    public void onEnable() {turnOn = true;}
    public void onDisable() {turnOn = false;}
}
