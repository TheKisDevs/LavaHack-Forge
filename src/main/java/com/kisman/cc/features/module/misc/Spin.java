package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.*;

public class Spin extends Module {
    public static Spin instance;

    public Spin() {
        super("Spin", "RotatePlayer", Category.MISC);

        instance = this;
    }
}
