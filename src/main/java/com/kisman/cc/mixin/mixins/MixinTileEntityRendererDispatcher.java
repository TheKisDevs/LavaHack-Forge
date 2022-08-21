package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.RenderTileEntityEvent;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 19:56 of 17.08.2022
 */
@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {
    @Inject(
            method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V",
            at = @At("HEAD"),
            cancellable = true
    ) private void renderPre(
            TileEntity tileEntity,
            float partialTicks,
            int destroyStage,
            CallbackInfo ci
    ) {
        RenderTileEntityEvent event = new RenderTileEntityEvent.Pre(
                tileEntity
        );

        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V",
            at = @At("RETURN")
    ) private void renderPost(
            TileEntity tileEntity,
            float partialTicks,
            int destroyStage,
            CallbackInfo ci
    ) {
        RenderTileEntityEvent event = new RenderTileEntityEvent.Post(
                tileEntity
        );

        Kisman.EVENT_BUS.post(event);
    }
}
