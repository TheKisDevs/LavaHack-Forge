package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.renderer.chunk.RenderChunk;

/**
 * @author _kisman_
 * @since 22:27 of 03.12.2022
 */
public class EventRenderChunkContainer extends Event {
    public RenderChunk chunk;

    public EventRenderChunkContainer(RenderChunk chunk) {
        this.chunk = chunk;
    }
}
