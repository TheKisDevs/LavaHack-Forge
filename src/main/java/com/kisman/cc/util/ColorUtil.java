package com.kisman.cc.util;

import java.awt.*;

public class ColorUtil {
    public static float seconds = 2;
    public static float saturation =1;
    public static float briqhtness = 1;

    public int getColor() {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, briqhtness);
        return color;
    }

    public static float getSeconds() {
        return seconds;
    }

    public static void setSeconds(float seconds) {
        ColorUtil.seconds = seconds;
    }

    public static float getSaturation() {
        return saturation;
    }

    public static void setSaturation(float saturation) {
        ColorUtil.saturation = saturation;
    }

    public static float getBriqhtness() {
        return briqhtness;
    }

    public static void setBriqhtness(float briqhtness) {
        ColorUtil.briqhtness = briqhtness;
    }
}
