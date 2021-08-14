package com.kisman.cc.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11.*;

import java.awt.*;

public class RenderHelper {
    public static void drawCircle(int x, int y, int radius, int color) {
        GL11.glPushMatrix();

        color(color);
        GL11.glBegin(GL11.GL_POLYGON);

        for(int i = 0; i < 360; i++) {
            GL11.glVertex2i(
                x + (int) Math.sin(Math.toRadians(i)) + radius, 
                y + (int) Math.cos(Math.toRadians(i)) + radius
            );
        }
        
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    public static void drawColoredCircle(int x, int y, int radius) {
        GL11.glPushMatrix();

        GL11.glLineWidth(3.5f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i = 0; i < 360; i++) {
            color(-1);
            GL11.glVertex2i(x, y);

            color(Color.HSBtoRGB(i / 360f, 1, 1));
            GL11.glVertex2i(
                x + (int) Math.sin(Math.toRadians(i)) + radius, 
                y + (int) Math.cos(Math.toRadians(i)) + radius
            );
        }
        GL11.glEnd();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    } 

    public static void color(int argb) {
        float alpha = (argb >> 24 & 255) / 255f;
        float red = (argb >> 16 & 255) / 255f;
        float green = (argb >> 8 & 255) / 255f;
        float blue = (argb & 255) / 255f;

        GL11.glColor4f(red, green, blue, alpha);
    }
}
