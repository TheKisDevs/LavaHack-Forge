package com.kisman.cc.util;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Colour {
    public int r, g, b, a;
    public float r1, g1, b1, a1;

    private boolean isInt = true;

    public Colour(float[] hsb) {
        this(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
    } 

    public Colour(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.r1 = r / 255f;
        this.g1 = g / 255f;
        this.b1 = b / 255f;
        this.a1 = a / 255f;
        fixColorRange();
    }

    public Colour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
        this.r1 = r / 255f;
        this.g1 = g / 255f;
        this.b1 = b / 255f;
        this.a1 = 1;
        fixColorRange();
    }

    public Colour(Colour color, int a) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = a;
        this.r1 = r / 255f;
        this.g1 = g / 255f;
        this.b1 = b / 255f;
        this.a1 = a / 255f;
        fixColorRange();
    }

    public Colour(Color color, float a) {
        this(color);
        this.a = (int) a * 255;
        this.a1 = a;
        fixColorRange();
    }

    public Colour(float r, float g, float b) {
        this.r1 = r;
        this.g1 = g;
        this.b1 = b;
        this.r = ((int) r1 * 255);
        this.g = ((int) g1 * 255);
        this.b = ((int) b1 * 255);
        isInt = false;
        fixColorRange();
    }

    public Colour(float r, float g, float b, float a) {
        this.r1 = r;
        this.g1 = g;
        this.b1 = b;
        this.a1 = a;
        this.r = ((int) r1 * 255);
        this.g = ((int) g1 * 255);
        this.b = ((int) b1 * 255);
        this.a = ((int) a1 * 255);
        isInt = false;
        fixColorRange();
    }

    public Colour(int color) {
        this.r = ColorUtils.getRed(color);
        this.g = ColorUtils.getGreen(color);
        this.b = ColorUtils.getBlue(color);
        this.a = ColorUtils.getAlpha(color);
        this.r1 = r / 255f;
        this.g1 = g / 255f;
        this.b1 = b / 255f;
        this.a1 = a / 255f;
        fixColorRange();
    }

    public void nextColor() {
        float[] hsb = RGBtoHSB();
        double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
        rainbowState %= 360.0;
        hsb[0] = (float) (rainbowState / 360.0);
        setColour(Colour.fromHSB(hsb, a));
    }

    private void setColour(Colour color) {
        this.r = ColorUtils.getRed(color.r);
        this.g = ColorUtils.getGreen(color.g);
        this.b = ColorUtils.getBlue(color.b);
        this.a = ColorUtils.getAlpha(color.a);
        this.r1 = r / 255f;
        this.g1 = g / 255f;
        this.b1 = b / 255f;
        this.a1 = a / 255f;
        fixColorRange();
    }

    public static Colour fromHSB(float[] hsb, int alpha) {
        return new Colour(ColorUtils.injectAlpha(Color.getHSBColor(hsb[0], hsb[1], hsb[2]), alpha));
    }

    public float[] RGBtoHSB() {
        return Color.RGBtoHSB(r, g, b, null);
    }

    private void fixColorRange() {
        if(r > 255) r = 255;
        else if(r < 0) r = 0;
        if(g > 255) g = 255;
        else if(g < 0) g = 0;
        if(b > 255) b= 255;
        else if(b < 0) b = 0;
        if(a > 255) a = 255;
        else if(a< 0) a = 0;
        if(r1 > 1) r1 = 1;
        else if(r1 < 0) r1 = 0;
        if(g1 > 1) g1 = 1;
        else if(g1 < 0) g1 = 0;
        if(b1 > 1) b1 = 1;
        else if(b1 < 0) b1 = 0;
        if(a1 > 1) a1 = 1;
        else if(a1 < 0) a1 = 0;
    }

    public Colour(Color color) {
        this(color.getRGB());
    }

    public Color getColor() {
        return new Color(r, g, b, a);
    }

    public int getRGB() {
        return new Color(r, g, b, a).getRGB();
    }

    public float getR() {
        if(isInt) return (float) r / 255;
        else return r1;
    }

    public float getG() {
        if(isInt) return (float) g / 255;
        else return g1;
    }

    public float getB() {
        if(isInt) return (float) b / 255;
        else return b1;
    }

    public float getA() {
        if(isInt) return (float) a / 255;
        else return a1;
    }

    public int getAlpha() {
        if(isInt) return a;
        else return (int) a1 * 255;
    }

    public void glColor() {
        GlStateManager.color(r1, g1, b1, a1);
    }
}
