package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityTeleport;

public class TraceTeleport extends Module {
    public TraceTeleport() {
        super("TraceTeleport", "", Category.COMBAT);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityTeleport) {
            SPacketEntityTeleport packet = (SPacketEntityTeleport) event.getPacket();

            if(Math.abs(mc.player.posX - packet.getX()) > 500d || Math.abs(mc.player.posZ - packet.getZ()) > 500d) {
                String name = "Unknown";
                Entity entity = mc.world.getEntityByID(packet.getEntityId());
                if(entity != null) name = entity.getClass().getSimpleName();

                double distance = Math.sqrt(Math.pow(mc.player.posX - packet.getX(), 2d) + Math.pow(mc.player.posZ - packet.getZ(), 2d));

                String warn = String.format("Entity [%s] teleported to [%.2f, %.2f, %.2f], %.2f blocks away", name, packet.getX(), packet.getY(), packet.getZ(), distance);

                ChatUtils.warning(warn);

                Kisman.LOGGER.warn("kisman.cc - trace: " + warn);
            }
        }
    });
}
