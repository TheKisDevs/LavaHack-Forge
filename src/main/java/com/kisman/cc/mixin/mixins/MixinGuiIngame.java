package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.GuiIngame.WIDGETS_TEX_PATH;

@Mixin(value = GuiIngame.class, priority = 10000)
public class MixinGuiIngame extends Gui {
    @Shadow @Final public Minecraft mc;
    @Shadow protected void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {}

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) callbackInfo.cancel();
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
        if (NoRender.instance.isToggled() && NoRender.instance.overlay.getValBoolean()) callbackInfo.cancel();
    }
}
