package com.kisman.cc.util;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class RenderHelper {

  public static void drawCircle(double x, double y, double radius, int color) {
    glPushMatrix();

    color(color);

    glBegin(GL_POLYGON);
    for ( int i = 0; i < 360; i++ ) {
      glVertex2d(
        (int) (x + Math.sin(Math.toRadians(i)) * radius),
        (int) (y + Math.cos(Math.toRadians(i)) * radius)
      );
    }
    glEnd();

    glPopMatrix();
  }

  public static void drawColoredCircle(double x, double y, double radius) {
    glPushMatrix();

    //You don't have to do this in Minecraft
    glLineWidth(3.5f);

    glEnable(GL_LINE_SMOOTH);
    glShadeModel(GL_SMOOTH);

    glBegin(GL_LINE_STRIP);
    for ( int i = 0; i < 360; i++ ) {
      color(-1);
      glVertex2d(x, y);
      color(Color.HSBtoRGB(i / 360f, 1, 1));
      glVertex2d(
        (int) (x + Math.sin(Math.toRadians(i)) * radius),
        (int) (y + Math.cos(Math.toRadians(i)) * radius)
      );
    }
    glEnd();

    glShadeModel(GL_FLAT);
    glDisable(GL_LINE_SMOOTH);

    glPopMatrix();
  }

  public static void color(int argb) {

    float alpha = (argb >> 24 & 255) / 255f;
    float red = (argb >> 16 & 255) / 255f;
    float green = (argb >> 8 & 255) / 255f;
    float blue = (argb & 255) / 255f;

    glColor4f(red, green, blue, alpha);
  }

}
