/*
package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
    @Inject(method = "renderItemModel", at = @At(value = "HEAD"))
    private void renderItemModel(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if(Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && !stack.isEmpty()) {
            GlStateManager.translate(getSet("X").getValDouble(), getSet("Y").getValDouble(), getSet("Z").getValDouble());
            GlStateManager.rotate((float) (getSet("RotateX").getValDouble()), 1, 0, 0);
            GlStateManager.rotate((float) (getSet("RotateY").getValDouble()), 0, 1, 0);
            GlStateManager.rotate((float) (getSet("RotateZ").getValDouble()), 0, 0, 1);
        }
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
    }
}
*/
