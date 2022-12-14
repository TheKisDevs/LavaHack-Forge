package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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

    private final Map<Block, List<BlockPos>> map = new ConcurrentHashMap<>();

    private int lastDimension = -1;

    @Override
    public void onDisable() {
        super.onDisable();
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

    private void reload(){
        Thread thread = new Thread(() -> {
            for(ChunkPos pos : sortChunks(getChunks())){
                map.putAll(loadChunk(pos));
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
