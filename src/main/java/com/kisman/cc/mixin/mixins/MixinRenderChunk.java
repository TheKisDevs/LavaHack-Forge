package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 22:28 of 03.12.2022
 */
@Mixin(RenderChunk.class)
public class MixinRenderChunk {
    @Inject(method = "setPosition", at = @At("RETURN"))
    private void setPosition(int x, int y, int z, CallbackInfo callbackInfo) {
        Kisman.EVENT_BUS.post(new EventRenderChunk((RenderChunk) (Object) this, new BlockPos(x, y, z)));
    }
}
