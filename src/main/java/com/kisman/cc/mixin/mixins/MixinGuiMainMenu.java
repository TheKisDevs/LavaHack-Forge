package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiMainMenu.class})
public class MixinGuiMainMenu extends GuiScreen {
    @Inject(method = {"drawScreen"}, at = @At("TAIL"))
    public void drawText(CallbackInfo ci){
        mc.fontRenderer.drawStringWithShadow(TextFormatting.WHITE + Kisman.NAME + " " + TextFormatting.GRAY + Kisman.VERSION, 1, 1, -1);
        mc.fontRenderer.drawStringWithShadow(TextFormatting.WHITE + "made by " + TextFormatting.GRAY + "_kisman_#5039", 1,mc.fontRenderer.FONT_HEIGHT +1, -1);
    }
}
