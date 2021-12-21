package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.awt.*;

@Mixin(RenderItem.class)
public class MixinRenderItem {
    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {}

    @ModifyArg(method = "renderEffect", at = @At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index=1)
    private int renderEffect(int oldValue) {
        return ViewModel.instance.isToggled() ? new Color(255, 255, 255, ViewModel.instance.alpha.getValInt()).getRGB() : oldValue;
    }

    @Redirect(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void POOOOOP(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        if (ViewModel.instance.isToggled()) {
            this.renderModel(model, new Color(255, 255, 255, ViewModel.instance.alpha.getValInt()).getRGB(), stack);
        } else {
            this.renderModel(model, -1, stack);
        }
    }

    @Redirect(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    private void renderItem(float colorRed, float colorGreen, float colorBlue, float alpha) {
        if (ViewModel.instance.isToggled()) {
            GlStateManager.color(1, 1, 1, ViewModel.instance.alpha.getValFloat() / 255f);
        } else {
            GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);
        }
    }
}