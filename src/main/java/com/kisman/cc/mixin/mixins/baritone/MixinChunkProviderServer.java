package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IChunkProviderServer;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Brady
 * @since 9/4/2018
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer implements IChunkProviderServer {
    @Shadow @Final public IChunkLoader chunkLoader;
    @Override public IChunkLoader getChunkLoader() {
        return this.chunkLoader;
    }
}
