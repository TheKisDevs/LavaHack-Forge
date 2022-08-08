package com.kisman.cc.features.module.Debug;

import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.mixin.mixins.accessor.ISPacketEntityTeleport;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketEntityTeleport;

public class ChorusTP extends Module {

    public ChorusTP(){
        super("ChorusTP", Category.DEBUG, true);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(mc.player == null || mc.world == null)
            return;

        if(!this.isToggled())
            return;

        if(!(event.getPacket() instanceof SPacketEntityTeleport))
            return;

        SPacketEntityTeleport packet = (SPacketEntityTeleport) event.getPacket();

        if(packet.getEntityId() != mc.player.getEntityId())
            return;

        ((ISPacketEntityTeleport) packet).setPosX(mc.objectMouseOver.getBlockPos().getX() + 0.5);
        ((ISPacketEntityTeleport) packet).setPosY(mc.objectMouseOver.getBlockPos().getY() + 1.0);
        ((ISPacketEntityTeleport) packet).setPosZ(mc.objectMouseOver.getBlockPos().getZ() + 0.5);
    });
}
