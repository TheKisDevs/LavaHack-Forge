package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BlockSearcher extends Module {

    private final Setting radius = register(new Setting("Radius", this, 4, 1, 16, true));

    public BlockSearcher(){
        super("BlockSearcher", Category.RENDER);
    }

    private final Map<Block, Color> colorMap = new ConcurrentHashMap<>();

    private final Map<ChunkPos, Map<Block, List<BlockPos>>> map = new ConcurrentHashMap<>();

    private int lastDimension = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
        lastDimension = -1;
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null){
            lastDimension = -1;
            return;
        }

        if(lastDimension == -1){
            lastDimension = mc.player.dimension;
            reload();
        }

        if(lastDimension != -1 && lastDimension != mc.player.dimension){
            lastDimension = mc.player.dimension;
            reload();
        }
    }

    @EventHandler
    private final Listener<PacketEvent.PostReceive> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketChunkData){
            SPacketChunkData packet = (SPacketChunkData) event.getPacket();
            ChunkPos pos = new ChunkPos(packet.getChunkX(), packet.getChunkZ());
            map.put(pos, loadChunk(pos));
        }
        if(event.getPacket() instanceof SPacketUnloadChunk){
            SPacketUnloadChunk packet = (SPacketUnloadChunk) event.getPacket();
            ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());
            map.remove(pos);
        }
        if(event.getPacket() instanceof SPacketBlockChange){
            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            ChunkPos pos = new ChunkPos(packet.getBlockPosition().getX() >> 4, packet.getBlockPosition().getZ() >> 4);
            Map<Block, List<BlockPos>> subMap = map.computeIfAbsent(pos, t -> new ConcurrentHashMap<>());
            List<BlockPos> list = subMap.computeIfAbsent(packet.getBlockState().getBlock(), t -> new Vector<>());
            list.add(packet.getBlockPosition());
            map.put(pos, subMap);
        }
        if(event.getPacket() instanceof SPacketMultiBlockChange){
            SPacketMultiBlockChange packet = (SPacketMultiBlockChange) event.getPacket();
            for(SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()){
                ChunkPos pos = new ChunkPos(data.getPos().getX() >> 4, data.getPos().getZ() >> 4);
                Map<Block, List<BlockPos>> subMap = map.computeIfAbsent(pos, t -> new ConcurrentHashMap<>());
                List<BlockPos> list = subMap.computeIfAbsent(data.getBlockState().getBlock(), t -> new Vector<>());
                list.add(data.getPos());
                map.put(pos, subMap);
            }
        }
    });

    private void reload(){
        Thread thread = new Thread(() -> {
            for(ChunkPos pos : sortChunks(getChunks())){
                map.put(pos, loadChunk(pos));
            }
        });
        thread.start();
    }

    private List<ChunkPos> sortChunks(List<ChunkPos> chunks){
        return chunks.stream()
                .sorted(Comparator.comparingDouble(o -> o.getDistanceSq(mc.player)))
                .collect(Collectors.toList());
    }

    private List<ChunkPos> getChunks(){
        List<ChunkPos> list = new Vector<>();
        for(int x = mc.player.chunkCoordX - radius.getValInt(); x <= mc.player.chunkCoordX + radius.getValInt(); x++){
            for(int z = mc.player.chunkCoordZ - radius.getValInt(); z <= mc.player.chunkCoordZ + radius.getValInt(); z++){
                list.add(new ChunkPos(x, z));
            }
        }
        return list;
    }

    private Map<Block, List<BlockPos>> loadChunk(ChunkPos chunkPos){
        Map<Block, List<BlockPos>> map = new ConcurrentHashMap<>();
        for(int x = chunkPos.getXStart(); x <= chunkPos.getXEnd(); x++){
            for(int z = chunkPos.getZStart(); z <= chunkPos.getZEnd(); z++){
                for(int y = 0; y < 256; y++){
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState blockState = mc.world.getBlockState(pos);
                    List<BlockPos> list = map.computeIfAbsent(blockState.getBlock(), t -> new Vector<>());
                    list.add(pos);
                }
            }
        }
        return map;
    }
}
