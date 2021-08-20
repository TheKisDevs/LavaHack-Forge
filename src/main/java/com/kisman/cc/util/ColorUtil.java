package com.kisman.cc.util;

import java.awt.*;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.ColorPicker;

public class ColorUtil {
    public static float seconds = 2;
    public static float saturation = 1;
    public static float briqhtness = 1;

    public int r;
    public int g;
    public int b;
    public int a;

    public int color;

    public ColorPicker colorPicker;

    public int getColor() {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, briqhtness);
        this.color = color;
        return color;
    }

    public void  getColorPickerRainBow(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
        if(colorPicker.isRainbowState()) {
            double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
            rainbowState %= 360.0;
            colorPicker.setColor(0, (float) (rainbowState / 360.0)); 
        }
    }

    public void rainbowLine() {
        if(colorPicker.isRainbowState()) {
            double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
            rainbowState %= 360.0;
            setLineColor((float) (rainbowState / 360.0), colorPicker.getColor(3));
        }
    }

    public void setLineColor(float hue, float alpha) {
        alpha(
            new Color(
                Color.HSBtoRGB(
                    hue, 
                    colorPicker.getColor(1), 
                    colorPicker.getColor(2)
                )
            ),
            alpha
        );

        ClickGui.setRLine(r);
        ClickGui.setGLine(g);
        ClickGui.setBLine(b);
        ClickGui.setALine(a);
    }

    public int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        r = (int) red;
        g = (int) green;
        b = (int) blue;
        a = (int) alpha;
        return new Color(red, green, blue, alpha).getRGB();
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
