package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.ArrayList;
import java.util.Arrays;

public class Fly extends Module {
    private Setting mode = new Setting("Mode", this, "Vanilla", new ArrayList<>(Arrays.asList("Vanilla", "Matrix")));
    private Setting vanillaLine = new Setting("VanillaLine", this, "Vanilla");

    private float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);

        setmgr.rSetting(mode);
        setmgr.rSetting(vanillaLine);
        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));
    }

    public void onEnable() {
        if(mc.player == null && mc.world == null) return;

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        }
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        this.flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        } else {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.1f);
        }

        if(mode.getValString().equalsIgnoreCase("Matrix")) {
            if(mc.gameSettings.keyBindJump.pressed) {
                mc.player.jump();
                mc.player.motionY -= 0.25f;
                if(mc.gameSettings.keyBindForward.pressed) {
                    mc.timer.elapsedTicks = (int) 1.05f;
                    mc.player.motionX *= 1.1f;
                    mc.player.motionZ *= 1.1f;
                    mc.player.onGround = false;
                }
            }
        }
    }

    public void onDisable() {
        if(mc.player == null && mc.world == null) return;

        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.1f);
        mc.timer.elapsedTicks = 1;
    }
}
