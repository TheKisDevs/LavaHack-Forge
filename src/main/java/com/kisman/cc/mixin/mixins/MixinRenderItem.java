package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.ItemCharms;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(RenderItem.class)
public class MixinRenderItem {
    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {}

    @ModifyArg(method = "renderEffect", at = @At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index=1)
    private int renderEffect(int oldValue) {
        return ItemCharms.instance.isToggled() ? ItemCharms.instance.color.getRGB() : oldValue;
    }

    @Redirect(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void POOOOOP(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        if (ItemCharms.instance.isToggled()) {
            ItemCharms m = ItemCharms.instance;
            this.renderModel(model, new Color(m.red.getRed(), m.green.getGreen(), m.blue.getBlue(), m.alpha.getAlpha()).getRGB(), stack);
        } else {
            this.renderModel(model, -1, stack);
        }
    }

    @Redirect(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    private void renderItem(float colorRed, float colorGreen, float colorBlue, float alpha) {
        if (ItemCharms.instance.isToggled()) {
            ItemCharms m = ItemCharms.instance;
            GlStateManager.color(m.red.getRed(), m.green.getGreen(), m.blue.getBlue(), m.alpha.getAlpha());
        } else {
            GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);
        }
    }
}
