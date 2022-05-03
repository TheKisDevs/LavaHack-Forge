package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;

public class Spin extends Module {
    public static Spin instance;

    public Spin() {
        super("Spin", "RotatePlayer", Category.MISC);

        instance = this;
    }
}
