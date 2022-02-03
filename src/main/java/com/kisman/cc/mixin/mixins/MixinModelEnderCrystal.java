package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.CrystalModifier;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ModelEnderCrystal.class, priority = 10000)
public class MixinModelEnderCrystal {
    @Final @Shadow private ModelRenderer cube;
    @Final @Shadow private ModelRenderer glass;
    @Shadow private ModelRenderer base;

    /**
     * @author
     */
    @Overwrite
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.translate((float)0.0f, (float)-0.5f, (float)0.0f);
        if (this.base != null) {
            this.base.render(scale);
        }
        GlStateManager.rotate(limbSwingAmount, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((float)0.0f, (float)(0.8f + ageInTicks), (float)0.0f);
        GlStateManager.rotate((float)60.0f, (float)0.7071f, (float)0.0f, (float)0.7071f);
        if (CrystalModifier.instance.isToggled()) {
            if (CrystalModifier.instance.outsideCube.getValBoolean()) {
                this.glass.render(scale);
            }
        } else {
            this.glass.render(scale);
        }
        float f = 0.875f;
        GlStateManager.scale((float)0.875f, (float)0.875f, (float)0.875f);
        GlStateManager.rotate((float)60.0f, (float)0.7071f, (float)0.0f, (float)0.7071f);
        GlStateManager.rotate((float)limbSwingAmount, (float)0.0f, (float)1.0f, (float)0.0f);
        if (CrystalModifier.instance.isToggled()) {
            if (CrystalModifier.instance.outsideCube2.getValBoolean()) {
                this.glass.render(scale);
            }
        } else {
            this.glass.render(scale);
        }
        GlStateManager.scale((float)0.875f, (float)0.875f, (float)0.875f);
        GlStateManager.rotate((float)60.0f, (float)0.7071f, (float)0.0f, (float)0.7071f);
        GlStateManager.rotate((float)limbSwingAmount, (float)0.0f, (float)1.0f, (float)0.0f);
        if (CrystalModifier.instance.isToggled()) {
            if (CrystalModifier.instance.insideCube.getValBoolean()) {
                this.cube.render(scale);
            }
        } else {
            this.cube.render(scale);
        }
        GlStateManager.popMatrix();
    }
}
