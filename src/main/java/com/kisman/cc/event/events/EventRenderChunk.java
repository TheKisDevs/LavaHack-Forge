package com.kisman.cc.event.events;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

/**
 * @author _kisman_
 * @since 22:29 of 03.12.2022
 */
public class EventRenderChunk {
    public RenderChunk chunk;
    public BlockPos pos;

    public EventRenderChunk(RenderChunk chunk, BlockPos pos) {
        this.chunk = chunk;
        this.pos = pos;
    }
}
