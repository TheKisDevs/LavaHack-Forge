package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.ArrayList;
import java.util.Arrays;

public class Fly extends Module {

    private Setting mode = new Setting("Mode", this, "Vanilla", new ArrayList<>(Arrays.asList("Vanilla", "Matrix")));


    private Setting vanillaLine = new Setting("VanullaLine", this, "Vanilla");


    private Setting motionLine = new Setting("MotionLine", this, "Motion");

    private Setting glider = new Setting("Glider", this, true);
    private Setting autoUp = new Setting("AutoUp", this, true);
    private Setting autoDown = new Setting("AutoDown", this, true);
    private Setting motionSpeed = new Setting("Speed", this, 5, 1, 8, false);
    private Setting glide = new Setting("Glide", this, 0.03, 0.01, 0.1, false);

    private float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("Mode", this, "Vanilla", new ArrayList<>(Arrays.asList("Vanilla", "Motion", "Matrix"))));

        setmgr.rSetting(vanillaLine);
        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));

        setmgr.rSetting(motionLine);
        setmgr.rSetting(glider);
        setmgr.rSetting(autoUp);
        setmgr.rSetting(autoDown);
        setmgr.rSetting(motionSpeed);
        setmgr.rSetting(glide);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(receiveListener);

        if(mc.player == null && mc.world == null) return;

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        }
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        this.flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();

        if(!mc.player.capabilities.isFlying && mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.isFlying = true;
        }

        if(mode.getValString().equalsIgnoreCase("Vanilla")) {
            mc.player.capabilities.setFlySpeed(flySpeed);
        }

        if(!mode.getValString().equalsIgnoreCase("Vanulla")) {
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
        //TODO: fix fly start

        Kisman.EVENT_BUS.unsubscribe(receiveListener);

        if(mc.player == null && mc.world == null) return;

        if(mc.player.capabilities.isFlying) {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.1f);
        }

        mc.timer.elapsedTicks = 1;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook && mode.getValString().equalsIgnoreCase("Mineland")) {
            event.cancel();
        }
    });
}
