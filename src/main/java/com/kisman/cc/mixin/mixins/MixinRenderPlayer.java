package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Shadow public ResourceLocation getEntityTexture(AbstractClientPlayer abstractClientPlayer) {return null;}

    @Inject(method = "preRenderCallback*", at = @At("HEAD"))
    public void renderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        if(Spin.instance.isToggled()) {
            float f = 0.9357f;
            float hue = (float) (System.currentTimeMillis() % 22600L) / 5.0f;

            GlStateManager.scale(f, f, f);

            GlStateManager.rotate(hue, 1, 0, hue);
        } else if(Reverse.instance.isToggled() && !Spin.instance.isToggled()) GlStateManager.rotate(180, 1, 0, 0);
    }
}
