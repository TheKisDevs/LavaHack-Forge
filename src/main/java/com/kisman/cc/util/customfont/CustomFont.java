package com.kisman.cc.util.customfont;

import net.minecraft.client.renderer.texture.DynamicTexture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CustomFont extends AbstractFontRenderer {
    private final float imgSize = 512.0f;
    protected CharData[] charData = new CharData[2000];
    protected Font font;
    protected boolean antiAlias;
    protected boolean fractionalMetrics;
    public int fontHeight = -1;
    protected int charOffset = 0;
    protected DynamicTexture tex;

    public CustomFont(Font font, boolean antiAlias, boolean fractionalMetrics){
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        this.tex = setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars){
        BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);
        try {
            return new DynamicTexture(img);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars){
        int imgSize = 512;
        BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imgSize, imgSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        FontMetrics fontMetrics = g.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        for(int i = 0; i < chars.length; i ++){
            char ch = (char) i;
            CharData charData = new CharData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
            charData.width = (dimensions.getBounds()).width + 8;
            charData.height = (dimensions.getBounds()).height;
            if (positionX + charData.width >= imgSize) {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }
            if (charData.height > charHeight) charHeight = charData.height;
            charData.storedX = positionX;
            charData.storedY = positionY;
            if (charData.height > this.fontHeight) this.fontHeight = charData.height;
            chars[i] = charData;
            g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width;
        }
        return bufferedImage;
    }

    @Override
    public void drawChar(CharData @NotNull [] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        //TODO доделать и это возвращает краш бвт
        /*if(!font.canDisplay(c) && CustomFontUtilKt.Companion.getFallbackFont() != null) {
            CustomFontUtilKt.Companion.getFallbackFont().drawChar(chars, c, x, y);
            return;
        }*/
        try {
            drawQuad(x, y, (chars[c]).width, (chars[c]).height, (chars[c]).storedX, (chars[c]).storedY, (chars[c]).width, (chars[c]).height);
        } catch (Exception e) {e.printStackTrace();}
    }

    protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        float renderSRCX = srcX / 512.0F;
        float renderSRCY = srcY / 512.0F;
        float renderSRCWidth = srcWidth / 512.0F;
        float renderSRCHeight = srcHeight / 512.0F;
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((x + width), y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, (y + height));
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, (y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((x + width), (y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((x + width), y);
    }

    public int getStringWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) if (c < this.charData.length) width += (this.charData[c]).width - 8 + this.charOffset;
        return width / 2;
    }

    public void setAntiAlias(boolean antiAlias) {
        if (this.antiAlias != antiAlias) {
            this.antiAlias = antiAlias;
            this.tex = setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
        }
    }

    public boolean isFractionalMetrics() {
        return this.fractionalMetrics;
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if (this.fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            this.tex = setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
        }
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
        this.tex = setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
    }

    @Override public void drawStringWithShadow(@NotNull String text, int x, int y, int color) {}
    @Override public void drawLine(int x, int y, int x1, int y1) {}
    @Override public float drawString(@NotNull String text, double x, double y, int color, boolean shadow) {return 0;}
    @Override public void drawCenteredString(@NotNull String text, float x, float y, int color) {}
    @Override public void drawCenteredStringWithShadow(@NotNull String text, float x, float y, int color) {}
    @Override public int getHeight() {return 0;}
    @Override public float drawString(@NotNull String text, double x, double y, int color) {return 0;}
    @Override public boolean getFractionMetrics() {return false;}
    @Override public boolean getAntiAlias() {return false;}

    public static class CharData {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
