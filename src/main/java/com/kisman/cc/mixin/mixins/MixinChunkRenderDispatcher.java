package com.kisman.cc.mixin.mixins;

import com.kisman.cc.mixin.accessors.IChunkRenderDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author _kisman_
 * @since 22:40 of 02.03.2023
 */
@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher implements IChunkRenderDispatcher {
    @Shadow private void uploadDisplayList(BufferBuilder bufferBuilderIn, int list, RenderChunk chunkRenderer) { }

    @Override
    public void handleUploadDisplayList(BufferBuilder bufferBuilderIn, int list, RenderChunk chunkRenderer) {
        uploadDisplayList(bufferBuilderIn, list, chunkRenderer);
    }
}
