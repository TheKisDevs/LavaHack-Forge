package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.CrystalModifier;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelEnderCrystal.class, priority = 10000)
public class MixinModelEnderCrystal {
    @Final @Shadow private ModelRenderer cube;
    @Final @Shadow private ModelRenderer glass;
    @Shadow private ModelRenderer base;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void doRender1(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if(CrystalModifier.instance.isToggled()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.translate(0.0f, -0.5f, 0.0f);
            if (this.base != null) this.base.render(scale);
            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, (0.8f + ageInTicks), 0.0f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            if (CrystalModifier.instance.isToggled()) if (CrystalModifier.instance.outsideCube.getValBoolean()) this.glass.render(scale);
            else this.glass.render(scale);
            float f = 0.875f;
            GlStateManager.scale(0.875f, 0.875f, 0.875f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            if (CrystalModifier.instance.isToggled()) if (CrystalModifier.instance.outsideCube2.getValBoolean()) this.glass.render(scale);
            else this.glass.render(scale);
            GlStateManager.scale(0.875f, 0.875f, 0.875f);
            GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
            GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
            if (CrystalModifier.instance.isToggled()) if (CrystalModifier.instance.insideCube.getValBoolean()) this.cube.render(scale);
            else this.cube.render(scale);
            GlStateManager.popMatrix();

            ci.cancel();
        }
    }
}
