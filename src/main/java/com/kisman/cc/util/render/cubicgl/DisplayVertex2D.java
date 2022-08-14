package com.kisman.cc.util.render.cubicgl;

import org.lwjgl.opengl.Display;

public class DisplayVertex2D {

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    private DisplayVertex2D(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public static DisplayVertex2D of(int x1, int y1, int x2, int y2){
        return new DisplayVertex2D(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
    }

    public static DisplayVertex2D ofNormalized(double x1, double y1, double x2, double y2){
        return of((int) (x1 * Display.getWidth()), (int) y1 * Display.getHeight(), (int) (x2 * Display.getWidth()), (int) (y2 * Display.getHeight()));
    }

    public static DisplayVertex2D ofNormalizedCenter(double x1, double y1, double x2, double y2){
        return ofNormalized((x1 + 1.0) / 2.0, (y1 + 1.0) / 2.0, (x2 + 1.0) / 2.0, (y2 + 1.0) / 2.0);
    }
}
