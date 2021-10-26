package com.kisman.cc.util;


import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Colour {
    public int r;
    public int g;
    public int b;
    public int a;

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

    public Color getColor() {
        return new Color(r, g, b, a);
    }

    public int getRGB() {
        return new Color(r, g, b, a).getRGB();
    }

    public float getR() {
        return r / 255;
    }

    public float getG() {
        return g / 255;
    }

    public float getB() {
        return b / 255;
    }

    public float getA() {
        return a / 255;
    }

    public int getAlpha() {
        return a;
    }

    public void glColor() {
        GlStateManager.color(r / 255, g / 255, b / 255, a / 255);
    }
}
