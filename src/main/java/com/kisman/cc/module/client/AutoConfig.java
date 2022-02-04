package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.settings.Setting;

public class AutoConfig extends Module {
    private Setting server = new Setting("Server", this, Servers.Sti);

    public AutoConfig() {
        super("AutoConfig[beta]", Category.CLIENT);

        setmgr.rSetting(server);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) return;

        switch (server.getValString()) {
            case "crystalpvpcc":
                Step.instance.height.setValDouble(2);
                ReverseStep.instance.height.setValDouble(2);
                AutoRer.instance.syns.setValBoolean(false);
                Speed.instance.speedMode.setValString("Sunrise Strafe");
                break;
            case "Sti":
                Step.instance.height.setValDouble(4);
                ReverseStep.instance.height.setValDouble(4);
                Speed.instance.speedMode.setValString("BHop");
                break;
            case "SunRise":
                Step.instance.setToggled(false);
                ReverseStep.instance.setToggled(false);
                Speed.instance.speedMode.setValString("Sunrise Strafe");
                AutoArmor.instance.setToggled(false);
                break;
        }
    }

    public enum Servers {crystalpvpcc, SunRise, Sti}
}
