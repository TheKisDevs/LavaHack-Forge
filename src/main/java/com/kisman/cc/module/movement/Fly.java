package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class Fly extends Module {
    private Setting mode = new Setting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "WellMore"));

    private float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));
        setmgr.rSetting(mode);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        this.flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        } else {
            if(mc.player.onGround) mc.player.motionY = 1.0;
            else {
                mc.player.capabilities.isFlying = true;
                mc.player.capabilities.setFlySpeed(1.3f);
                mc.player.motionX = 0.0;
                mc.player.motionY = -0.02;
                mc.player.motionZ = 0.0;
            }
        }
    }

    public void onDisable() {
        if(mc.player == null && mc.world == null) return;

        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.1f);
    }
}
