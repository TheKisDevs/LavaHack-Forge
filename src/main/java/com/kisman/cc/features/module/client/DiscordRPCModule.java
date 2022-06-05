package com.kisman.cc.features.module.client;

import com.kisman.cc.RPC;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.enums.RPCImages;

public class DiscordRPCModule extends Module {
    public static DiscordRPCModule instance;

    public Setting impr = register(new Setting("Impr RPC", this, true));

    public Setting imgMode = register(new Setting("Image Mode", this, RPCImages.LavaHake));

    public DiscordRPCModule() {
        super("DiscordRPC", "", Category.CLIENT);
        instance = this;

        super.setToggled(true);
    }

    public void onEnable() {RPC.startRPC();}
    public void onDisable() {RPC.stopRPC();}
}