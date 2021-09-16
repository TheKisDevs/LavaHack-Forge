package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class Fly extends Module {

    float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);
        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));
    }

    public void update() {
        flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();
        if(mc.player != null && mc.world != null) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        }

    }

    public void onDisable() {
        if(mc.player != null && mc.world != null) {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.1f);
        }
    }
}
