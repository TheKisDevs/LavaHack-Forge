package com.kisman.cc.util;

import net.minecraft.client.gui.Gui;

public class Render2DUtil {
    public void drawLine(int x, int y, int length, DrawLineMode drawLineMode, int color) {
        if(drawLineMode == DrawLineMode.VERTICAL) {
            Gui.drawRect(x, y, x + 1, y + length, color);
        } else {
            Gui.drawRect(x, y, x + length, y + 1, color);
        }
    }
}
