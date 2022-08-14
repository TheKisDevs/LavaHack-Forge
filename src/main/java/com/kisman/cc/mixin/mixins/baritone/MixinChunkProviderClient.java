package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IChunkProviderClient;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkProviderClient.class)
public class MixinChunkProviderClient implements IChunkProviderClient {
    @Shadow @Final private Long2ObjectMap<Chunk> chunkMapping;
    @Override public Long2ObjectMap<Chunk> loadedChunks() {
        return this.chunkMapping;
    }
}
