package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    private Minecraft mc = Minecraft.getMinecraft();

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {

        if (Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(getSet("RightX").getValDouble(), getSet("RightY").getValDouble(), getSet("RightZ").getValDouble());
            GlStateManager.rotate((float) (getSet("RotateRightX").getValDouble()), 1, 0, 0);
            GlStateManager.rotate((float) (getSet("RotateRightY").getValDouble()), 0, 1, 0);
            GlStateManager.rotate((float) (getSet("RotateRightZ").getValDouble()), 0, 0, 1);
        }
        if (Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.LEFT) {
            GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
            GlStateManager.rotate((float) (getSet("RotateLeftX").getValDouble()), 1, 0, 0);
            GlStateManager.rotate((float) (getSet("RotateLeftY").getValDouble()), 0, 1, 0);
            GlStateManager.rotate((float) (getSet("RotateLeftZ").getValDouble()), 0, 0, 1);
        }

/*        if (!Kisman.instance.moduleManager.getModule("ViemModel").isToggled() && hand == EnumHandSide.RIGHT) {
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
        }*/
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViemModel"), name);
    }
}
