package com.kisman.cc.features.schematica.schematica.world;

import com.kisman.cc.features.schematica.schematica.world.storage.SaveHandlerSchematic;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class WorldDummy extends World {
    private static WorldDummy instance;

    protected WorldDummy(ISaveHandler saveHandler, WorldInfo worldInfo, WorldProvider worldProvider, Profiler profiler, boolean client) {
        super(saveHandler, worldInfo, worldProvider, profiler, client);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }

    public static WorldDummy instance() {
        if (instance == null) {
            WorldSettings worldSettings = new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT);
            WorldInfo worldInfo = new WorldInfo(worldSettings, "FakeWorld");
            instance = new WorldDummy(new SaveHandlerSchematic(), worldInfo, new WorldProviderSchematic(), new Profiler(), false);
        }

        return instance;
    }
}
