package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.*;

public class Reverse extends Module {
    public static Reverse instance;

    public Reverse() {
        super("Reverse", "Reverse", Category.MISC);

        instance = this;
    }
}
