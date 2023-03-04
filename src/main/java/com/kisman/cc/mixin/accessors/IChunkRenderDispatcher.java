package com.kisman.cc.mixin.accessors;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;

/**
 * @author _kisman_
 * @since 22:40 of 02.03.2023
 */
public interface IChunkRenderDispatcher {
    void handleUploadDisplayList(BufferBuilder bufferBuilderIn, int list, RenderChunk chunkRenderer);
}
