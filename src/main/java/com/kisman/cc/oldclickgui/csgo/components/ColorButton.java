package com.kisman.cc.oldclickgui.csgo.components;

import com.kisman.cc.module.client.*;
import com.kisman.cc.oldclickgui.ColorPicker;
import com.kisman.cc.oldclickgui.csgo.*;
import com.kisman.cc.oldclickgui.csgo.Window;
import com.kisman.cc.util.*;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class ColorButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;

    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;

    private Colour value;
    public boolean syns;

    private ColorPicker colorPicker;

    private ValueChangeListener<Colour> listener;
    private ValueChangeListener<Boolean> listenerS;

    public ColorButton(IRenderer renderer, float[] color, int preferredWidth, int preferredHeight) {
        super(renderer);
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.colorPicker = new ColorPicker();

        setWidth(preferredWidth);
        setHeight(preferredHeight);

        colorPicker.setColor(color);
        colorPicker.bool1 = true;
    }

    public ColorButton(IRenderer renderer, float[] color) {
        this(renderer, color, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        colorPicker.syns = syns;
        if(!colorPicker.syns) {
            value = (
                    getColour(
                            new Color(
                                    Color.HSBtoRGB(
                                            colorPicker.getColor(0),
                                            colorPicker.getColor(1),
                                            colorPicker.getColor(2)
                                    )
                            ),
                            colorPicker.getColor(3)
                    )
            );
        } else {
            value = ColorModule.instance.synsColor.getColour();
        }

        renderer.drawRect(x, y, getWidth(), getHeight(), value.getColor());
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 1) {
            updateHovered(x, y, offscreen);

            if (hovered) Minecraft.getMinecraft().displayGuiScreen(colorPicker);
        }
        return false;
    }

    public Colour getValue() {
        return value;
    }

    public void setValue(Colour value) {
        this.value = value;
    }

    public void setListener(ValueChangeListener<Colour> listener) {
        this.listener = listener;
    }

    public void setSynsListener(ValueChangeListener<Boolean> listener) {
        this.listenerS = listener;
    }

    public int getR(Color color, float alpha) {
        return color.getRed();
    }

    public int getG(Color color, float alpha) {
        return color.getGreen();
    }

    public int getB(Color color, float alpha) {
        return color.getBlue();
    }
    //        this.selectedColorFinal = alpha(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])), this.color[3]);
    // alpha(new Color(Color.HSBtoRGB(colorPicker.getColor(0), colorPicker.getColor(1), colorPicker.getColor(2))), colorPicker.getColor(3));
    final int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha).getRGB();
    }

    final int colour(Color color, float alpha, int index) {
        final float red = (float) color.getRed() / 255f;
        final float green = (float) color.getGreen() / 255f;
        final float blue = (float) color.getBlue() / 255f;

        if(index == 1) {
            return new Color(red, green, blue, alpha).getRed();
        } else if(index == 2) {
            return new Color(red, green, blue, alpha).getGreen();
        } else if(index == 3) {
            return new Color(red, green, blue, alpha).getBlue();
        } else if(index == 4) {
            return new Color(red, green, blue, alpha).getAlpha();
        } else {
            return 5;
        }
    }

    final Colour getColour(Color color, float alpha) {
        final int alpha1eee = (int) (alpha * 255);

        return new Colour(color.getRed(), color.getGreen(), color.getGreen(), alpha1eee);
    }
}
