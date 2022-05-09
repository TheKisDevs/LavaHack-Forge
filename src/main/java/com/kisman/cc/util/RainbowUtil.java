package com.kisman.cc.util;

import java.awt.*;

public class RainbowUtil {

    public static Color rainbow(long offset, int sat, int bright, int alpha, double speed){
        int hue = (int) ((System.currentTimeMillis() + offset) % (11520L / speed)) / 32;
        if(hue > 360)
            hue = 360;
        int rgb = fromHSB(hue, sat, bright);
        int val = ((alpha & 0xff) << 24) | rgb;
        return new Color(val, true);
    }

    public static Colour rainbow2(int hOffset, final int sat, final int bright, final int alpha, double speed){
        int hue = (int) (((System.currentTimeMillis()) % (11520L / speed)) / 32) + hOffset;
        if(hue > 360)
            hue = hue - 360;
        int rgb = fromHSB(hue, sat, bright);
        return new Colour(rgb).withAlpha(alpha);
    }

    /**
     * Color space conversion from HSB to RGB
     * @author Cubic
     */
    private static int fromHSB(int hue, int sat, int bright){
        final float step = 4.25f;
        float r = 0;
        float g = 0;
        float b = 0;
        int k = hue / 60;
        switch (k) {
            case 0:
                r = 255;
                g = hue * step;
                b = 0;
                break;
            case 1:
                r = 255f - ((hue - 60) * step);
                g = 255;
                b = 0;
                break;
            case 2:
                r = 0;
                g = 255;
                b = (hue - 120) * step;
                break;
            case 3:
                r = 0;
                g = 255f - ((hue - 180) * step);
                b = 255;
                break;
            case 4:
                r = (hue - 240) * step;
                g = 0;
                b = 255;
                break;
            case 5:
            case 6:
                r = 255;
                g = 0;
                b = 255f - ((hue - 300) * step);
                break;
        }
        if(bright > 50){
            float m = 0.02f * (bright - 50);
            r += (255f - r) * m;
            g += (255f - g) * m;
            b += (255f - b) * m;
        } else {
            float m = 0.02f * bright;
            r *= m;
            g *= m;
            b *= m;
        }
        float s = bright * 2.55f;
        float u = 0.01f * (100 - sat);
        r += (s - r) * u;
        g += (s - g) * u;
        b += (s - b) * u;
        int rVal = Math.round(r);
        int gVal = Math.round(g);
        int bVal = Math.round(b);
        return 0xff000000 | ((rVal & 0xff) << 16) | ((gVal & 0xff) << 8) | (bVal & 0xff);
    }
}
