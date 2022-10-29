package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEnchantGlintColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {

    @Redirect(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    private static void onRenderEnchantedGlint(float r, float g, float b, float a){
        int red = Math.round(r * 255f);
        int green = Math.round(g * 255f);
        int blue = Math.round(b * 255f);
        int alpha = Math.round(a * 255f);
        EventEnchantGlintColor event = new EventEnchantGlintColor(EventEnchantGlintColor.Stage.Armor, new Color(red, green, blue, alpha));
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()){
            Color color = event.getColor();
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            return;
        }
        GlStateManager.color(r, g, b, a);
    }
}
