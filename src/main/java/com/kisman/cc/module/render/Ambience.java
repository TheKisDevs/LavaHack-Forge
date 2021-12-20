package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;

import java.awt.*;

public class Ambience extends Module {
    public static Ambience instance;

    public Setting red = new Setting("Red", this, 255, 0, 255, false);
    public Setting green = new Setting("Green", this, 255, 0, 255, false);
    public Setting blue = new Setting("Blue", this, 255, 0, 255, false);
    public Setting alpha = new Setting("Alpha", this, 255, 0, 255, false);
    public Setting light = new Setting("Light", this, false);
    public Setting time = new Setting("Time", this, 24, 5, 25, true);
    public Setting infinity = new Setting("InfinityCyrcle", this, true);
    public Setting speed = new Setting("Speed", this, 100, 10, 1000, true);

    public Setting useSaturation = new Setting("UseSaturation", this, false);
    public Setting saturation = new Setting("Saturation", this, 0.5, 0, 1, false);

    public int cyrcle = 0;

    public Ambience() {
        super("Ambience", "minecraqft color", Category.RENDER);

        instance = this;

        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(alpha);
        setmgr.rSetting(light);
        setmgr.rSetting(time);
        setmgr.rSetting(infinity);
        setmgr.rSetting(speed);
        setmgr.rSetting(useSaturation);
        setmgr.rSetting(saturation);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    public void update() {
        if(mc.world == null) return;

        cyrcle += speed.getValInt();
        mc.world.setWorldTime(infinity.getValBoolean() ? cyrcle : time.getValLong() * 1000L);
        if(cyrcle >= 24000) cyrcle = 0;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketTimeUpdate) {
            event.cancel();
        }
    });

    public Color getColor() {
        return new Color(red.getValInt(), green.getValInt(),  blue.getValInt(),  alpha.getValInt());
    }
}
