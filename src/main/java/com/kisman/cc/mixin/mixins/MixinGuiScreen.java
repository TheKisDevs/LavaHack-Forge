//package com.kisman.cc.mixin.mixins;
//
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//
//@Mixin({Gui.class})
//public class MixinGuiScreen extends GuiScreen{
//    @Inject(method = {"drawGradientRect"}, at = {@At("TAIL")}, cancellable = false)
//    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
//    {
//        float f = (float)(startColor >> 24 & 255) / 255.0F;
//        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
//        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
//        float f3 = (float)(startColor & 255) / 255.0F;
//        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
//        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
//        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
//        float f7 = (float)(endColor & 255) / 255.0F;
//        GlStateManager.disableTexture2D();
//        GlStateManager.enableBlend();
//        GlStateManager.disableAlpha();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        GlStateManager.shadeModel(7425);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferbuilder = tessellator.getBuffer();
//        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.pos((double)right, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
//        bufferbuilder.pos((double)left, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
//        bufferbuilder.pos((double)left, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
//        bufferbuilder.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
//        tessellator.draw();
//        GlStateManager.shadeModel(7424);
//        GlStateManager.disableBlend();
//        GlStateManager.enableAlpha();
//        GlStateManager.enableTexture2D();
//    }
//}
