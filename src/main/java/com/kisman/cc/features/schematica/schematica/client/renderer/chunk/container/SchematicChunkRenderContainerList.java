package com.kisman.cc.features.schematica.schematica.client.renderer.chunk.container;

import com.kisman.cc.features.schematica.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.kisman.cc.features.schematica.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import org.lwjgl.opengl.GL11;

public class SchematicChunkRenderContainerList extends SchematicChunkRenderContainer {
    @Override
    public void renderChunkLayer(final BlockRenderLayer layer) {
        if (this.initialized) {
            for (final RenderChunk renderChunk : this.renderChunks) {
                final ListedRenderChunk listedRenderChunk = (ListedRenderChunk) renderChunk;
                GlStateManager.pushMatrix();
                preRenderChunk(renderChunk);
                GL11.glCallList(listedRenderChunk.getDisplayList(layer, listedRenderChunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }

            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }

    @Override
    public void renderOverlay() {
        if (this.initialized) {
            for (final RenderOverlay renderOverlay : this.renderOverlays) {
                final RenderOverlayList renderOverlayList = (RenderOverlayList) renderOverlay;
                GlStateManager.pushMatrix();
                preRenderChunk(renderOverlay);
                GL11.glCallList(renderOverlayList.getDisplayList(BlockRenderLayer.TRANSLUCENT, renderOverlayList.getCompiledChunk()));
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.resetColor();
        this.renderOverlays.clear();
    }
}
