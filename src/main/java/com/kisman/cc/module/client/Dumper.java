package com.kisman.cc.module.client;

import com.kisman.cc.module.*;

public class Dumper extends Module   {
    public static Dumper instance;

    public Dumper() {
        super("Dumper", "gay exploit", Category.CLIENT);

        instance = this;
    }
}
