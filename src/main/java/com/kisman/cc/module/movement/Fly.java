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

    private String mode;

    private float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("Mode", this, "Vanilla", new ArrayList<>(Arrays.asList("Vanilla", "MineLand"))));

        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));
    }

    public void onEnable() {
        if(mc.player == null && mc.world == null) return;

        this.mode = Kisman.instance.settingsManager.getSettingByName(this, "Mode").getValString();

        Kisman.EVENT_BUS.subscribe(receiveListener);

        if(mc.player != null && mc.world != null) {
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        }
    }

    public void update() {
        this.mode = Kisman.instance.settingsManager.getSettingByName(this, "Mode").getValString();

        this.flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();
    }

    public void onDisable() {
        //TODO: fix fly start

        Kisman.EVENT_BUS.unsubscribe(receiveListener);

        if(mc.player != null && mc.world != null) {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.1f);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook && this.mode.equalsIgnoreCase("Mineland")) {
            event.cancel();
        }
    });
}
