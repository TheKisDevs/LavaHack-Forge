package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.util.minecraft.PacketsKt;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketUnloadChunk;

import java.util.List;
import java.util.Vector;

/**
 * @author Cubic
 * @since 13.8.2022
 */
@ModuleInfo(
        name = "NoChunkUnload",
        category = Category.MOVEMENT
)
public class NoChunkUnload extends Module {
    public List<SPacketUnloadChunk> packets = new Vector<>();

    @Override
    public void onEnable(){
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        packets.clear();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        for(SPacketUnloadChunk packet : packets)
            PacketsKt.processPacket(packet);
        packets.clear();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(!(event.getPacket() instanceof SPacketUnloadChunk))
            return;
        SPacketUnloadChunk packet = (SPacketUnloadChunk) event.getPacket();
        packets.add(packet);
        event.cancel();
    });
}
