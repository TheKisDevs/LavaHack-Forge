package com.kisman.cc.module.client;

import com.kisman.cc.module.*;

public class ClientFont extends Module {
    public static ClientFont instance;

    public ClientFont() {
        super("ClientFont", "yes i do", Category.CLIENT);

        instance = this;
    }
}
