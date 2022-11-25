package com.kisman.cc.features.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.SPacketUpdateHealth;

@SuppressWarnings("ALL")
public class HealthCancel extends Module {

    public HealthCancel(){
        super("HealthCancel", Category.DEBUG);
    }

    private Packet<INetHandler> lastHealthUpdate = null;

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null)
            return;
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        if(mc.myNetworkManager != null && mc.myNetworkManager.isChannelOpen()){
            try {
                lastHealthUpdate.processPacket(mc.myNetworkManager.getNetHandler());
            } catch (ThreadQuickExitException ignored){
            }
        }
        lastHealthUpdate = null;
    }

    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketUpdateHealth){
            lastHealthUpdate = event.getPacket();
            event.cancel();
        }
    });
}
