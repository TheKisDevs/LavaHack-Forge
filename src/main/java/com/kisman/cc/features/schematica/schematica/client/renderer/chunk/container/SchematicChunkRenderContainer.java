package com.kisman.cc.features.schematica.schematica.client.renderer.chunk.container;

import com.kisman.cc.features.schematica.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.ChunkRenderContainer;

import java.util.List;

public abstract class SchematicChunkRenderContainer extends ChunkRenderContainer {
    protected List<RenderOverlay> renderOverlays = Lists.newArrayListWithCapacity(16 * 33 * 33);

    @Override
    public void initialize(double viewEntityX, double viewEntityY, double viewEntityZ) {
        super.initialize(viewEntityX, viewEntityY, viewEntityZ);
        this.renderOverlays.clear();
    }

    public void addRenderOverlay(final RenderOverlay renderOverlay) {
        this.renderOverlays.add(renderOverlay);
    }

    public abstract void renderOverlay();
}
