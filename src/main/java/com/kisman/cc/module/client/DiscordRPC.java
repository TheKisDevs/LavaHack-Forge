package com.kisman.cc.module.client;

import com.kisman.cc.RPC;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class DiscordRPC extends Module {
    public static DiscordRPC instance;

    public DiscordRPC() {
        super("DiscordRPC", "", Category.CLIENT);

        super.setToggled(true);

        instance = this;
    }

    public void onEnable() {
        RPC.startRPC();
    }

    public void onDisable() {
        RPC.stopRPC();
    }
}
