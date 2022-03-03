package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.oldclickgui.particle.ParticleSystem;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.modules.CustomMainMenu;
import com.kisman.cc.viaforge.ViaForge;
import com.kisman.cc.viaforge.gui.GuiProtocolSelector;
import com.kisman.cc.viaforge.protocol.ProtocolCollection;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMainMenu.class, priority = 10000)
public class MixinGuiMainMenu extends GuiScreen {
    private ParticleSystem particleSystem;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        buttonList.add(new GuiButton(1337, 1, 2 + CustomFontUtil.getFontHeight() * 2 + 1, 98, 20, ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));
        particleSystem = new ParticleSystem(300);
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void injectActionPerformed(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.id == 1337) mc.displayGuiScreen(new GuiProtocolSelector(this));
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawText(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        //text
        CustomFontUtil.drawStringWithShadow(TextFormatting.WHITE + Kisman.getName() + " " + TextFormatting.GRAY + Kisman.getVersion(), 1, 1, -1);
        CustomFontUtil.drawStringWithShadow(TextFormatting.WHITE + "made by " + TextFormatting.GRAY + "_kisman_#5039", 1,CustomFontUtil.getFontHeight() + 2, -1);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void down(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if(CustomMainMenu.PARTICLES) {
            particleSystem.tick(10);
            particleSystem.render();
            particleSystem.onUpdate();
        }
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V"))
    private void injectForCustomSplash(GuiMainMenu instance, FontRenderer fontRenderer, String s, int x, int y, int color) {
        String customSplash = CustomMainMenu.CUSTOM_SPLASH_TEXT ? CustomMainMenu.getRandomCustomSplash() : s;
        if(CustomMainMenu.CUSTOM_SPLASH_FONT) CustomFontUtil.drawCenteredStringWithShadow(customSplash, x, y, color);
        else instance.drawCenteredString(fontRenderer, customSplash, x, y, color);
    }
}
