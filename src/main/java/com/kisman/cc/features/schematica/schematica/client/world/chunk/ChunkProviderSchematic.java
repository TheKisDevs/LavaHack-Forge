package com.kisman.cc.features.schematica.schematica.client.world.chunk;

import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.google.common.base.MoreObjects;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// FIXME: `extends ChunkProviderClient` is required for the `WorldClient.getChunkProvider` method to work properly
@MethodsReturnNonnullByDefault
public class ChunkProviderSchematic extends ChunkProviderClient implements IChunkProvider {
    private final SchematicWorld world;
    private final Chunk emptyChunk;
    private final Map<Long, ChunkSchematic> chunks = new ConcurrentHashMap<Long, ChunkSchematic>();

    public ChunkProviderSchematic(final SchematicWorld world) {
        super(world);
        this.world = world;
        this.emptyChunk = new EmptyChunk(world, 0, 0) {
            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }

    private boolean chunkExists(final int x, final int z) {
        return x >= 0 && z >= 0 && x < this.world.getWidth() && z < this.world.getLength();
    }

    @Override
    public Chunk getLoadedChunk(final int x, final int z) {
        if (!chunkExists(x, z)) {
            return this.emptyChunk;
        }

        final long key = ChunkPos.asLong(x, z);

        ChunkSchematic chunk = this.chunks.get(key);
        if (chunk == null) {
            chunk = new ChunkSchematic(this.world, x, z);
            this.chunks.put(key, chunk);
        }

        return chunk;
    }

    @Override
    public Chunk provideChunk(final int x, final int z) {
        return getLoadedChunk(x, z);
    }

    @Override
    public String makeString() {
        return "SchematicChunkCache";
    }

    // ChunkProviderClient
    @Override
    public Chunk loadChunk(int x, int z) {
        return MoreObjects.firstNonNull(getLoadedChunk(x, z), this.emptyChunk);
    }

    // ChunkProviderClient
    @Override
    public void unloadChunk(int x, int z) {
        // NOOP: schematic chunks are part of the schematic world and are never unloaded separately
    }
}
