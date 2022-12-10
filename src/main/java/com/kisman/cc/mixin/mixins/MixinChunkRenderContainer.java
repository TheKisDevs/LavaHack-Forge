package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderChunkContainer;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 22:25 of 03.12.2022
 */
@Mixin(ChunkRenderContainer.class)
public class MixinChunkRenderContainer {
    @Inject(method = "preRenderChunk", at = @At(value = "RETURN", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;getPosition()Lnet/minecraft/util/math/BlockPos/MutableBlockPos;"))
    private void preRenderChunk(RenderChunk renderChunk, CallbackInfo callbackInfo) {
        Kisman.EVENT_BUS.post(new EventRenderChunkContainer(renderChunk));
    }
}
