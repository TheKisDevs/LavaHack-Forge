package com.kisman.cc.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Render2DUtil{// extends GuiScreen
    static Minecraft mc = Minecraft.getMinecraft();

    public static void drawLine(int x, int y, int length, DrawLineMode drawLineMode, int color) {
        if(drawLineMode == DrawLineMode.VERTICAL) {
            Gui.drawRect(x, y, x + 1, y + length, color);
        } else {
            Gui.drawRect(x, y, x + length, y + 1, color);
        }
    }

    public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(texture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    public static void drawBox(int x1, int y1, int x2, int y2, int thickness, Color color) {
        // Gui.drawRect(x1, y1, x2, x1 + thickness, color.getRGB());
        // Gui.drawRect(x1, y1, x1 + thickness, y2, color.getRGB());
        // Gui.drawRect(x2 - thickness, y1, x2, y2, color.getRGB());
        // Gui.drawRect(x1, y2 - thickness, x2, y2, color.getRGB());

        Gui.drawRect(x1, y1, x2, y2, new Color(71, 67, 67, 150).getRGB());
        //Gui.drawRect(1, 1, 101, 101, new Color(255, 255, 255, 255).getRGB());
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        double j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
