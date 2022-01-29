package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.combat.AutoRer;
import com.kisman.cc.module.movement.ReverseStep;
import com.kisman.cc.module.movement.Speed;
import com.kisman.cc.module.movement.Step;
import com.kisman.cc.settings.Setting;

public class AutoConfig extends Module {
    private Setting server = new Setting("Server", this, Servers.Sti);

    public AutoConfig() {
        super("AutoConfig", Category.CLIENT);

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
                break;
            case "SunRise":
                Step.instance.setToggled(false);
                ReverseStep.instance.setToggled(false);
                Speed.instance.speedMode.setValString("Sunrise Strafe");
                break;
        }
    }

    public enum Servers {crystalpvpcc, SunRise, Sti}
}
