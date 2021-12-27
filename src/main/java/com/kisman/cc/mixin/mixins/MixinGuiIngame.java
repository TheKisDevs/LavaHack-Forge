package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRender2D;
import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngame.class, priority = 10000)
public class MixinGuiIngame {
    @Inject(method = "renderGameOverlay", at = @At("HEAD"), cancellable = true)
    private void renderGameOverlay(float p_renderGameOverlay_1_, CallbackInfo ci) {
        EventRender2D event = new EventRender2D(new ScaledResolution(Minecraft.getMinecraft()), p_renderGameOverlay_1_);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) {
            callbackInfo.cancel();
        }
    }
}
