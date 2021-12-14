package com.kisman.cc.util;


import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Colour {
    public int r, g, b, a;
    public float r1, g1, b1, a1;

    private boolean isInt = true;

    public Colour(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Colour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public Colour(Colour color, int a) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = a;
    }

    public Colour(float r, float g, float b) {
        this.r1 = r;
        this.g1 = g;
        this.b1 = b;
        isInt = false;
    }

    public Colour(float r, float g, float b, float a) {
        this.r1 = r;
        this.g1 = g;
        this.b1 = b;
        this.a1 = a;
        isInt = false;
    }

    public Colour(int color) {
        this.r1 = ColorUtils.getRed(color);
        this.g1 = ColorUtils.getGreen(color);
        this.b1 = ColorUtils.getBlue(color);
        this.a1 = 1;
        isInt = false;
    }

    public Color getColor() {
        if(isInt) {
            return new Color(r, g, b, a);
        } else {
            return new Color(r1 * 255, g1 * 255, b1 * 255, a1 * 255);
        }
    }

    public int getRGB() {
        if(isInt) {
            return new Color(r, g, b, a).getRGB();
        } else {
            return new Color(r1 * 255, g1 * 255, b1 * 255, a1 * 255).getRGB();
        }
    }

    public float getR() {
        if(isInt) {
            return (float) r / 255;
        } else {
            return r1;
        }
    }

    public float getG() {
        if(isInt) {
            return (float) g / 255;
        } else {
            return g1;
        }
    }

    public float getB() {
        if(isInt) {
            return (float) b / 255;
        } else {
            return b1;
        }
    }

    public float getA() {
        if(isInt) {
            return (float) a / 255;
        } else {
            return a1;
        }
    }

    public int getAlpha() {
        if(isInt) {
            return a;
        } else {
            return (int) a1 * 255;
        }
    }

    public void glColor() {
        if(isInt) {
            GlStateManager.color(r / 255f, g / 255f, b / 255f, a / 255f);
        } else {
            GlStateManager.color(r1, g1, b1, a1);
        }
    }
}
