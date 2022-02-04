package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.SandBox;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMainMenu.class, priority = 10000)
public class MixinGuiMainMenu extends GuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        Kisman.instance.sandBoxShaders.init();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawText(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        //sandbox
        if(SandBox.instance.isToggled() && Kisman.instance.sandBoxShaders.currentshader != null) {
            GlStateManager.disableCull();

            Kisman.instance.sandBoxShaders.currentshader.useShader(width * 2, height * 2, mouseX * 2, mouseY * 2, (System.currentTimeMillis() - Kisman.instance.sandBoxShaders.time) / 1000f);

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(-1f, 1f);
            GL11.glVertex2f(1f, 1f);
            GL11.glVertex2f(1f, -1f);
            GL11.glEnd();
            GL20.glUseProgram(0);
        }

        //text
        mc.fontRenderer.drawStringWithShadow(TextFormatting.WHITE + Kisman.getName() + " " + TextFormatting.GRAY + Kisman.getVersion(), 1, 1, -1);
        mc.fontRenderer.drawStringWithShadow(TextFormatting.WHITE + "made by " + TextFormatting.GRAY + "_kisman_#5039", 1,mc.fontRenderer.FONT_HEIGHT + 1, -1);
    }

    //sandbox
    @Inject(method = "renderSkybox", at = @At("HEAD"), cancellable = true)
    public void renderSkybox(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if(SandBox.instance.isToggled() && Kisman.instance.sandBoxShaders.currentshader != null) ci.cancel();
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal = 0))
    public void drawGradientRect1(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
        if(!SandBox.instance.isToggled() && Kisman.instance.sandBoxShaders.currentshader == null) drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal = 1))
    public void drawGradientRect2(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
        if(!SandBox.instance.isToggled() && Kisman.instance.sandBoxShaders.currentshader == null) drawGradientRect(left, top, right, bottom, startColor, endColor);
    }
}
