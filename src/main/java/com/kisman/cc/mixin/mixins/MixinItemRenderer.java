package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.render.SwingAnimation;
import com.kisman.cc.module.render.ViemModel;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    private Minecraft mc = Minecraft.getMinecraft();

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        int rotateMainX = 0;
        int rotateMainY = 0;
        int rotateMainZ = 0;

        int rotateOffX = 0;
        int rotateOffY = 0;
        int rotateOffZ = 0;

        boolean isSwing = mc.player.swingProgress > 0 && SwingAnimation.instance.isToggled() && SwingAnimation.instance.mode.getValString().equalsIgnoreCase("Strong");

        if(isSwing) {
            rotateMainX = 72;
            rotateMainY = 180;
            rotateMainZ = 240;

            rotateOffX = 0;
            rotateOffY = 300;
            rotateOffZ = 77;
        } else if(mc.player.swingProgress == 0) {
            rotateMainX = 0;
            rotateMainY = 0;
            rotateMainZ = 0;

            rotateOffX = 0;
            rotateOffY = 0;
            rotateOffZ = 0;
        }

        if (Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(getSet("RightX").getValDouble(), getSet("RightY").getValDouble(), getSet("RightZ").getValDouble());
            GlStateManager.rotate(isSwing ? (float) rotateMainX : (!ViemModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (getSet("RotateRightX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate(isSwing ? (float) rotateMainY : (!ViemModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (getSet("RotateRightY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate(isSwing ? (float) rotateMainZ : (!ViemModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (getSet("RotateRightZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViemModel.instance.scaleRightX.getValDouble(), ViemModel.instance.scaleRightY.getValDouble(), ViemModel.instance.scaleRightZ.getValDouble());
        }
        if (Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.LEFT) {
            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
            GlStateManager.rotate(isSwing ? (float) rotateOffX : (!ViemModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (getSet("RotateLeftX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate(isSwing ? (float) rotateOffY : (!ViemModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (getSet("RotateLeftY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate(isSwing ? (float) rotateOffZ : (!ViemModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (getSet("RotateLeftZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViemModel.instance.scaleLeftX.getValDouble(), ViemModel.instance.scaleLeftY.getValDouble(), ViemModel.instance.scaleLeftZ.getValDouble());
        }
        if (!Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(-0.2785682F, 0.18344387F, 0.15731531F);
            GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(35.3F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-9.785F, 0.0F, 0.0F, 1.0F);
        }
        if (!Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.LEFT) {
            GlStateManager.translate(-0.2785682F, 0.18344387F, 0.15731531F);
            GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(35.3F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-9.785F, 0.0F, 0.0F, 1.0F);
        }
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
    }
}
