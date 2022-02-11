package com.kisman.cc.util;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.glow.ShaderShell;
import com.kisman.cc.util.render.objects.*;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/* 
* @author _kisman_
* @credits github.com/TheKisDevs/loader
*/
public class Render2DUtil extends GuiScreen {
    public static Render2DUtil instance = new Render2DUtil();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public double getZLevel() {return this.zLevel;}

    public static void drawAbstract(AbstractObject drawing) {
        if(drawing != null) drawing.render();
    }

    public static void drawAbstract(ObjectWithGlow drawing) {
        if(drawing != null) drawing.render();
    }

    public static void drawAbstract(AbstractGradient drawing) {
        if(drawing != null) drawing.render();
    }

    public static void drawCircle(double cx, double cy, double radius, Color color, float width, int segments) {
        ColorUtils.glColor(color);
        glLineWidth(width);
        glBegin(GL_LINE_LOOP);
        for(int i = 0; i < segments; i++) {
            float theta = (float) (2f * Math.PI * i / segments);

            float x = (float) (radius * Math.cos(theta));
            float y = (float) (radius * Math.sin(theta));

            glVertex2d(x + cx, y + cy);
        }
        glEnd();
    }

    public static void drawProgressCircle(double cx, double cy, double radius, Color color, float width, double degrees, int segments) {
        ColorUtils.glColor(color);
        glLineWidth(width);
        glBegin(GL_LINE);
        for(int i = 0; i < segments; i++) {
            if(i > degrees) break;
            float theta = (float) (2f * Math.PI * i / segments);

            float x = (float) (radius * Math.cos(theta));
            float y = (float) (radius * Math.sin(theta));

            glVertex2d(x + cx, y + cy);
        }
        glEnd();
    }

    public static void drawLine(int x, int y, int length, DrawLineMode drawLineMode, int color) {
        if(drawLineMode == DrawLineMode.VERTICAL) Gui.drawRect(x, y, x + 1, y + length, color);
        else Gui.drawRect(x, y, x + length, y + 1, color);
    }

    public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(texture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    public static void drawBox(int x1, int y1, int x2, int y2, int thickness, Color color) {
        Gui.drawRect(x1, y1, x2, y2, new Color(71, 67, 67, 150).getRGB());
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

    public static void drawPolygonPart(final double x, final double y, final int radius, final int part, final int color, final int endcolor) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final float alpha2 = (endcolor >> 24 & 0xFF) / 255.0f;
        final float red2 = (endcolor >> 16 & 0xFF) / 255.0f;
        final float green2 = (endcolor >> 8 & 0xFF) / 255.0f;
        final float blue2 = (endcolor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        final double TWICE_PI = 6.283185307179586;
        for (int i = part * 90; i <= part * 90 + 90; ++i) {
            final double angle = 6.283185307179586 * i / 360.0 + Math.toRadians(180.0);
            bufferbuilder.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0.0).color(red2, green2, blue2, alpha2).endVertex();
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawVGradientRect(final float left, final float top, final float right, final float bottom, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0).color(f2, f3, f4, f).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f2, f3, f4, f).endVertex();
        bufferbuilder.pos(left, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGlow(final double x, final double y, final double x1, final double y1, final int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        drawVGradientRect((float)(int)x, (float)(int)y, (float)(int)x1, (float)(int)(y + (y1 - y) / 2.0), ColorUtil.injectAlpha(new Color(color), 0).getRGB(), color);
        drawVGradientRect((float)(int)x, (float)(int)(y + (y1 - y) / 2.0), (float)(int)x1, (float)(int)y1, color, ColorUtil.injectAlpha(new Color(color), 0).getRGB());
        final int radius = (int)((y1 - y) / 2.0);
        drawPolygonPart(x, y + (y1 - y) / 2.0, radius, 0, color, ColorUtil.injectAlpha(new Color(color), 0).getRGB());
        drawPolygonPart(x, y + (y1 - y) / 2.0, radius, 1, color, ColorUtil.injectAlpha(new Color(color), 0).getRGB());
        drawPolygonPart(x1, y + (y1 - y) / 2.0, radius, 2, color, ColorUtil.injectAlpha(new Color(color), 0).getRGB());
        drawPolygonPart(x1, y + (y1 - y) / 2.0, radius, 3, color, ColorUtil.injectAlpha(new Color(color), 0).getRGB());
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if(left) {
            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG= (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else instance.drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }

    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(right, top, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, top, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, bottom, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(right, bottom, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRoundedRect(double startX, double startY, double endX, double endY, Color color, double radius) {
        drawRoundedRect((float) startX - radius, (float) startY - radius, (float) endX + radius, (float) endY + radius, color.getRGB(), Config.instance.glowRadius.getValFloat());
    }

    public static void drawRoundedRect(double startX, double startY, double endX, double endY, int color, float radius) {
        drawRoundedRect((float) startX, (float) startY, (float) endX, (float) endY, color, radius);
    }

    public static void drawRoundedRect(float startX, float startY, float endX, float endY, int color, float radius) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        float alpha = ((float) (color >> 24 & 0xFF) / 255F);
        float red = (float) (color >> 16 & 0xFF) / 255F;
        float green = (float) (color >> 8 & 0xFF) / 255F;
        float blue = (float) (color & 0xFF) / 255F;
        ShaderShell.ROUNDED_RECT.attach();
        ShaderShell.ROUNDED_RECT.set4F("color", red, green, blue, alpha);
        ShaderShell.ROUNDED_RECT.set2F("resolution", Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        ShaderShell.ROUNDED_RECT.set2F("center", (startX + (endX - startX) / 2) * 2,
                (startY + (endY - startY) / 2) * 2);
        ShaderShell.ROUNDED_RECT.set2F("dst", (endX - startX - radius) * 2, (endY - startY - radius) * 2);
        ShaderShell.ROUNDED_RECT.set1F("radius", radius);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(endX, startY);
        GL11.glVertex2d(startX, startY);
        GL11.glVertex2d(startX, endY);
        GL11.glVertex2d(endX, endY);
        GL11.glEnd();
        ShaderShell.ROUNDED_RECT.detach();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawRoundedRect(float startX, float startY, float endX, float endY, int color) {
        drawRoundedRect(startX, startY, endX, endY, color, Config.instance.glowRadius.getValFloat());
    }

    public static void drawRoundedRect(double startX, double startY, double endX, double endY, Color color) {
        drawRoundedRect((float) startX, (float) startY, (float) endX, (float) endY, color.getRGB(), Config.instance.glowRadius.getValFloat());
    }

    public static void drawShadowSliders(double x, double y, double sliderWidth, double sliderHeight, int color, double lineWidth) {
        //slider base
        drawGradientRect(x, y, x + sliderWidth, y + sliderHeight, color, Color.BLACK.getRGB());

        //outline
        drawOutlineRect(x, y, x + sliderWidth, y + sliderHeight, lineWidth, color);
    }

    public static void drawOutlineRect(double x1, double y1, double x2, double y2, double lineWidth, int color) {
        drawRect(x1, y1, x2, y1 + lineWidth, color);
        drawRect(x1, y2 - lineWidth, x2, y2, color);
        drawRect(x1, y1, x1 + lineWidth, y2, color);
        drawRect(x2 - lineWidth, y1, x2, y2, color);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, instance.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, instance.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, instance.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, instance.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0, 1, 0 , 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0 , 1, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin , vMax, GL11.GL_NEAREST);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);

        GlStateManager.disableBlend();
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
                .pos(x, y+height, 0.0D)
                .tex(uMin, vMax).endVertex();
        worldrenderer
                .pos(x+width, y+height, 0.0D)
                .tex(uMax, vMax).endVertex();
        worldrenderer
                .pos(x+width, y, 0.0D)
                .tex(uMax, vMin).endVertex();
        worldrenderer
                .pos(x, y, 0.0D)
                .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }
}
