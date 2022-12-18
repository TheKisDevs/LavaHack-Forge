package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.thread.ThreadUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.util.math.ChunkPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InfiniteRender extends Module {

    private final Setting range = register(new Setting("Range", this, 64, 1, 512, true));

    public InfiniteRender(){
        super("InfiniteRender", "Keeps chunks rendered client side", Category.RENDER);
    }

    private final Set<ChunkPos> chunks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Map<ChunkPos, SPacketUnloadChunk> packets = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(this);
        mc.getTextureManager().tick();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @Override
    public void update() {
        ThreadUtils.async(() -> {
            Set<ChunkPos> toRemove = new HashSet<>();
            for(Map.Entry<ChunkPos, SPacketUnloadChunk> entry : packets.entrySet()){
                if(entry.getKey().getDistanceSq(mc.player) <= (range.getValInt() * range.getValInt()))
                    continue;
                entry.getValue().processPacket(mc.player.connection);
                toRemove.add(entry.getKey());
            }
            toRemove.forEach(packets::remove);
        });
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketChunkData){
            SPacketChunkData packet = (SPacketChunkData) event.getPacket();
            ChunkPos pos = new ChunkPos(packet.getChunkX(), packet.getChunkZ());
            if(chunks.contains(pos))
                return;
            chunks.add(pos);
            event.cancel();
        }
        if(event.getPacket() instanceof SPacketUnloadChunk){
            SPacketUnloadChunk packet = (SPacketUnloadChunk) event.getPacket();
            ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());
            if(pos.getDistanceSq(mc.player) > (range.getValInt() * range.getValInt())){
                chunks.remove(pos);
                return;
            }
            packets.put(pos, packet);
            event.cancel();
        }
    });
}
