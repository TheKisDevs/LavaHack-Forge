package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;

public class Reverse extends Module {
    public static Reverse instance;

    public Reverse() {
        super("Reverse", "Reverse", Category.MISC);

        instance = this;
    }
}
