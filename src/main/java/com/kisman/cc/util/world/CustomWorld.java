package com.kisman.cc.util.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;
import org.cubic.dynamictask.AbstractTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomWorld extends WorldClient {

    private final Map<String, AbstractTask<?>> overriders = new HashMap<>();

    private final WorldClient original;

    public CustomWorld(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn) {
        super(netHandler, settings, dimension, difficulty, profilerIn);
        this.original = null;
    }

    public CustomWorld(WorldClient worldClient){
        super(worldClient.connection, getWorldSettingFromWorld(worldClient), worldClient.provider.getDimension(), worldClient.getDifficulty(), worldClient.profiler);
        this.original = worldClient;
    }

    private static WorldSettings getWorldSettingFromWorld(WorldClient worldClient){
        WorldInfo worldInfo = worldClient.getWorldInfo();
        WorldSettings worldSettings = new WorldSettings(
                worldInfo.getSeed(),
                worldInfo.getGameType(),
                worldInfo.isMapFeaturesEnabled(),
                worldInfo.isHardcoreModeEnabled(),
                worldInfo.getTerrainType()
        );
        worldSettings.setGeneratorOptions(worldInfo.getGeneratorOptions());
        if(worldInfo.areCommandsAllowed())
            worldSettings.enableCommands();
        return worldSettings;
    }

    /*
    public static class ArgFetcher extends TaskArgumentFetcher {

        private final WorldClient world;

        private final WorldClient original;

        public ArgFetcher(Object[] args, Class<?>[] types, WorldClient world, WorldClient original) {
            super(args, types);
            this.world = world;
            this.original = original;
        }

        public WorldClient getWorld() {
            return world;
        }

        public WorldClient getOriginal() {
            return original;
        }
    }
     */

    public void override(String method, AbstractTask<?> task){
        //task.setArgumentFetcher((args, types) -> new ArgFetcher(args, types, this, original));
        overriders.put(method, task);
    }

    public void toDefault(String method){
        overriders.remove(method);
    }

    public WorldClient getOriginal() {
        return original;
    }

    @Override
    public @NotNull IBlockState getBlockState(@NotNull BlockPos pos){
        if(overriders.containsKey("getBlockState"))
            return (IBlockState) overriders.get("getBlockState").doTask(pos);
        return super.getBlockState(pos);
    }
}
