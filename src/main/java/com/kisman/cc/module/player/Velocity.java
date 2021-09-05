package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module{
    public Velocity() {
        super("Velocity", "akb", Category.PLAYER);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                event.cancel();
            }
        }
        if(event.getPacket() instanceof SPacketExplosion) {
            event.cancel();
        }
    });
}
