package com.kisman.cc.features.module.misc.announcer;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;

import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;

@ModuleInfo(
        name = "TraceTeleport",
        display = "Teleports",
        submodule = true
)
public class TraceTeleport extends Module {
    private final Setting onlyPlayers = register(new Setting("Only Players", this, true));
    
    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(receive);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(receive);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityTeleport) {
            SPacketEntityTeleport packet = (SPacketEntityTeleport) event.getPacket();

            Entity entity = mc.world.getEntityByID(packet.getEntityId());
            if((!onlyPlayers.getValBoolean() || entity instanceof EntityPlayer) && (Math.abs(mc.player.posX - packet.getX()) > 500d || Math.abs(mc.player.posZ - packet.getZ()) > 500d)) ChatUtility.warning().printClientModuleMessage(String.format("Entity [%s] teleported to [%.2f, %.2f, %.2f], %.2f blocks away", entity != null ? entity.getClass().getSimpleName() : "Unknown", packet.getX(), packet.getY(), packet.getZ(), Math.sqrt(Math.pow(mc.player.posX - packet.getX(), 2d) + Math.pow(mc.player.posZ - packet.getZ(), 2d))));
        }
    });
}
