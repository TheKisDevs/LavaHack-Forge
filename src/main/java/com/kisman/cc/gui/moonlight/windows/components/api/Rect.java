package com.kisman.cc.gui.moonlight.windows.components.api;

public class Rect {

    public int x, y, width, height;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isInside(int posX, int posY) {
        return posX > x && posY > y && posX < x + width && posY < y + height;
    }
}
