package com.kisman.cc.util.render;

import org.lwjgl.opengl.Display;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

public class GLUtil {

    private static final Map<Integer, Boolean> states = new ConcurrentHashMap<>();

    public static void original(int cap){
        states.put(cap, glIsEnabled(cap));
    }

    public static void recover(int cap){
        boolean b = states.get(cap);
        if(b)
            glEnable(cap);
        else
            glDisable(cap);
    }

    public static void scissors(int x1, int y1, int x2, int y2){
        int mx = Math.min(x1, x2);
        int my = Math.min(y1, y2);
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        glScissor(mx, my, dx, dy);
    }


    public static void scissorsNormalized(double x1, double y1, double x2, double y2){
        scissors((int) (x1 * Display.getWidth()), (int) (y1 * Display.getHeight()), (int) (x2 * Display.getWidth()), (int) (y2 * Display.getHeight()));
    }
}
