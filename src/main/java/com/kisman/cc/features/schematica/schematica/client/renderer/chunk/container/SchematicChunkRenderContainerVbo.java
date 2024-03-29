package com.kisman.cc.features.schematica.schematica.client.renderer.chunk.container;

import com.kisman.cc.features.schematica.schematica.client.renderer.chunk.overlay.RenderOverlay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockRenderLayer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SchematicChunkRenderContainerVbo extends SchematicChunkRenderContainer {
    @Override
    public void renderChunkLayer(BlockRenderLayer layer) {
        preRenderChunk();

        if (this.initialized) {
            for (RenderChunk renderChunk : this.renderChunks) {
                VertexBuffer vertexbuffer = renderChunk.getVertexBufferByLayer(layer.ordinal());
                GlStateManager.pushMatrix();
                preRenderChunk(renderChunk);
                renderChunk.multModelviewMatrix();
                vertexbuffer.bindBuffer();
                setupArrayPointers();
                vertexbuffer.drawArrays(GL11.GL_QUADS);
                GlStateManager.popMatrix();
            }

            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
            GlStateManager.resetColor();
            this.renderChunks.clear();
        }

        postRenderChunk();
    }

    private void preRenderChunk() {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
    }

    private void postRenderChunk() {
        List<VertexFormatElement> elements = DefaultVertexFormats.BLOCK.getElements();

        for (VertexFormatElement element : elements) {
            VertexFormatElement.EnumUsage usage = element.getUsage();
            int index = element.getIndex();

            switch (usage) {
            case POSITION:
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                break;

            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                break;

            case COLOR:
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                GlStateManager.resetColor();
                break;
            }
        }
    }

    private void setupArrayPointers() {
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void renderOverlay() {
        if (this.initialized) {
            preRenderOverlay();

            for (RenderOverlay renderOverlay : this.renderOverlays) {
                VertexBuffer vertexBuffer = renderOverlay.getVertexBufferByLayer(BlockRenderLayer.TRANSLUCENT.ordinal());
                GlStateManager.pushMatrix();
                preRenderChunk(renderOverlay);
                renderOverlay.multModelviewMatrix();
                vertexBuffer.bindBuffer();
                setupArrayPointersOverlay();
                vertexBuffer.drawArrays(GL11.GL_QUADS);
                GlStateManager.popMatrix();
            }

            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
            GlStateManager.resetColor();
            this.renderOverlays.clear();

            postRenderOverlay();
        }
    }

    private void preRenderOverlay() {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
    }

    private void postRenderOverlay() {
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    }

    private void setupArrayPointersOverlay() {
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 16, 0);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
    }
}
