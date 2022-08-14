package com.kisman.cc.features.module.movement;

import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.minecraft.PacketProcessor;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketUnloadChunk;

import java.util.List;
import java.util.Vector;

/**
 * @author Cubic
 * @since 13.8.2022
 */
public class NoChunkUnload extends Module {

    private final Setting packetsPerTick = register(new Setting("PacketsPerTicks", this, 5, 1, 50, true));
    private final Setting unlimited = register(new Setting("Unlimited", this, false));

    public NoChunkUnload(){
        super("NoChunkUnload", Category.MOVEMENT, true);
    }

    public List<SPacketUnloadChunk> packets = new Vector<>();

    @Override
    public void onEnable(){
        super.onEnable();
        packets.clear();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        for(SPacketUnloadChunk packet : packets)
            PacketProcessor.processPacket(packet);
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
