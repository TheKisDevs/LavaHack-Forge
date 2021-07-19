package com.kisman.cc.module.modules.player;

import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module{
    public Velocity() {
        super("Velocity", 0, Category.PLAYER);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == Minecraft.getMinecraft().player.getEntityId()) {
                event.cancel();
            }
        }
        if(event.getPacket() instanceof SPacketExplosion) {
            event.cancel();
        }
    });
}
