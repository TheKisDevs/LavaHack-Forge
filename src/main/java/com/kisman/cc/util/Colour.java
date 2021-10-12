package com.kisman.cc.util;


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
}
