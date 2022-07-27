package com.kisman.cc.features.schematica.schematica.client.renderer.chunk;

import com.kisman.cc.features.schematica.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("JavaReflectionInvocation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OverlayRenderDispatcher extends ChunkRenderDispatcher {
    public OverlayRenderDispatcher() {
        super();
    }

    public OverlayRenderDispatcher(int countRenderBuilders) {
        super(countRenderBuilders);
    }

    @Override
    public ListenableFuture<Object> uploadChunk(final BlockRenderLayer layer, final BufferBuilder buffer, final RenderChunk renderChunk, final CompiledChunk compiledChunk, final double distanceSq) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread() || OpenGlHelper.useVbo()) {
            return super.uploadChunk(layer, buffer, renderChunk, compiledChunk, distanceSq);
        }

        try {
            Method method = Class.forName("net.minecraft.client.renderer.chunk.ChunkRenderDispatcher")
                    .getMethod("uploadDisplayList", BufferBuilder.class, Integer.class, CompiledChunk.class, RenderChunk.class);
            method.setAccessible(true);
            method.invoke(this, buffer, ((RenderOverlayList) renderChunk).getDisplayList(layer, compiledChunk), renderChunk);
        } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        buffer.setTranslation(0.0, 0.0, 0.0);
        return Futures.immediateFuture(null);
    }
}