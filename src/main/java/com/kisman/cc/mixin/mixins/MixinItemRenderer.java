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
        boolean isSwingMain = isSwing && hand == EnumHandSide.RIGHT && SwingAnimation.instance.main.getValBoolean();
        boolean isSwingOff = isSwing && hand == EnumHandSide.LEFT && SwingAnimation.instance.off.getValBoolean();

        if(isSwing) {
            if(hand == EnumHandSide.RIGHT && SwingAnimation.instance.main.getValBoolean()) {
                rotateMainX = 72;
                rotateMainY = 180;
                rotateMainZ = 240;
            }

            if(hand == EnumHandSide.LEFT && SwingAnimation.instance.off.getValBoolean()) {
                rotateOffX = 0;
                rotateOffY = 300;
                rotateOffZ = 77;
            }
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
            GlStateManager.rotate(isSwingMain ? (float) rotateMainX : (!ViemModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (getSet("RotateRightX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate(isSwingMain ? (float) rotateMainY : (!ViemModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (getSet("RotateRightY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate(isSwingMain ? (float) rotateMainZ : (!ViemModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (getSet("RotateRightZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViemModel.instance.scaleRightX.getValDouble(), ViemModel.instance.scaleRightY.getValDouble(), ViemModel.instance.scaleRightZ.getValDouble());
        }
        if (Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.LEFT) {
            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
            GlStateManager.rotate(isSwingOff ? (float) rotateOffX : (!ViemModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (getSet("RotateLeftX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
            GlStateManager.rotate(isSwingOff ? (float) rotateOffY : (!ViemModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (getSet("RotateLeftY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
            GlStateManager.rotate(isSwingOff ? (float) rotateOffZ : (!ViemModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (getSet("RotateLeftZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
            GlStateManager.scale(ViemModel.instance.scaleLeftX.getValDouble(), ViemModel.instance.scaleLeftY.getValDouble(), ViemModel.instance.scaleLeftZ.getValDouble());
        }
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
    }
}
