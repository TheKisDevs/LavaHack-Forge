package com.kisman.cc.module.client;

import com.kisman.cc.RPC;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class DiscordRPC extends Module {
    public static DiscordRPC instance;

    public Setting showModules = new Setting("Show Enabled Modules", this, true);

    public DiscordRPC() {
        super("DiscordRPC", "", Category.CLIENT);
        instance = this;

        setmgr.rSetting(showModules);

        super.setToggled(true);
    }

    public void onEnable() {RPC.startRPC();}
    public void onDisable() {RPC.stopRPC();}
    public void update() {RPC.updateRPC();}
}
