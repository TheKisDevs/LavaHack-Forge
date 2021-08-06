package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.RPC;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;

public class RPCModule extends Module {
    ArrayList<String> rpcMode;

    public RPCModule() {
        super("DiscordRPC", "", Category.CLIENT);
        rpcMode = new ArrayList<>();
        rpcMode.add("HP");
        rpcMode.add("IP");
        Kisman.instance.settingsManager.rSetting(new Setting("RPC mode", this, "HP", rpcMode));
    }

    public void onEnable() {
        RPC.startRPC();
    }

    public void onDisable() {
        RPC.stopRPC();
    }

    public void update() {
        String mode = Kisman.instance.settingsManager.getSettingByName(this, "RPC mode").getValString();
        if(mode.equalsIgnoreCase("HP")) {
            com.kisman.cc.RPC.setIsHP(true);
        } else {
            com.kisman.cc.RPC.setIsHP(false);
        }
    }
}
