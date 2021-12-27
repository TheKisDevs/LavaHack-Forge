package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.BlockOutline;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 10000)
public class MixinRenderGlobal {
    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    public void onDrawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if(BlockOutline.instance.isToggled()) {
            ci.cancel();
        }
    }
}
