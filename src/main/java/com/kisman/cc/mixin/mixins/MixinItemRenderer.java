package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.render.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = ItemRenderer.class, priority = 10000)
public class MixinItemRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    private void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_) {}

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        if(ViewModel.instance.isToggled() || SwingAnimation.instance.isToggled()) {
            float rotateMainX = 0;
            float rotateMainY = 0;
            float rotateMainZ = 0;

            boolean isSwing = mc.player.swingProgress > 0 && SwingAnimation.instance.isToggled() && SwingAnimation.instance.mode.getValString().equalsIgnoreCase("Strong");
            boolean isSwingMain = isSwing && hand == EnumHandSide.RIGHT && (!SwingAnimation.instance.ignoreEating.getValBoolean() || !PlayerUtil.IsEating());

            if (isSwing) {
                switch ((SwingAnimation.StrongMode) SwingAnimation.instance.strongMode.getValEnum()) {
                    case Blockhit1: {
                        if(hand == EnumHandSide.RIGHT) {
                            rotateMainX = 72;
                            rotateMainY = 180;
                            rotateMainZ = 240;
                        }
                        break;
                    }
                    case Blockhit2: {
                        if (hand == EnumHandSide.RIGHT) {
                            rotateMainX = 344;
                            rotateMainY = 225;
                            rotateMainZ = 0;
                        }
                        break;
                    }
                }
            } else if (mc.player.swingProgress == 0) {
                rotateMainX = 0;
                rotateMainY = 0;
                rotateMainZ = 0;
            }

            if (Kisman.instance.moduleManager.getModule("ViewModel").isToggled() && hand == EnumHandSide.RIGHT) {
                GlStateManager.translate(getSet("RightX").getValDouble(), getSet("RightY").getValDouble(), getSet("RightZ").getValDouble());
                GlStateManager.rotate(isSwingMain ? rotateMainX : (!ViewModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (getSet("RotateRightX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
                GlStateManager.rotate(isSwingMain ? rotateMainY : (!ViewModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (getSet("RotateRightY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
                GlStateManager.rotate(isSwingMain ? rotateMainZ : (!ViewModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (getSet("RotateRightZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
                GlStateManager.scale(ViewModel.instance.scaleRightX.getValDouble(), ViewModel.instance.scaleRightY.getValDouble(), ViewModel.instance.scaleRightZ.getValDouble());
            }

            if (Kisman.instance.moduleManager.getModule("ViewModel").isToggled() && hand == EnumHandSide.LEFT) {
                GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
                GlStateManager.rotate((!ViewModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (getSet("RotateLeftX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
                GlStateManager.rotate((!ViewModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (getSet("RotateLeftY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
                GlStateManager.rotate((!ViewModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (getSet("RotateLeftZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
                GlStateManager.scale(ViewModel.instance.scaleLeftX.getValDouble(), ViewModel.instance.scaleLeftY.getValDouble(), ViewModel.instance.scaleLeftZ.getValDouble());
            }
        } else this.transformSideFirstPerson(hand, y);
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViewModel"), name);
    }
}