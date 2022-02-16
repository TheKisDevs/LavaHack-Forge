package com.kisman.cc.util.render.objects;

import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class AbstractGradient {
    public Vec4d vec;
    public Color start, end;
    public boolean vertical;
    public float width;

    public AbstractGradient(Vec4d vec, Color start, Color end, boolean vertical) {
        this.vec = vec;
        this.vertical = vertical;
        this.start = start;
        this.end = end;
    }

    public AbstractGradient(Vec4d vec, Color start, Color end) {
        this(vec, start, end, false);
    }

    public void render() {
        GL11.glPushMatrix();
        if(vertical) {
            final float startA = (start.getRGB() >> 24 & 0xFF) / 255.0f;
            final float startR = (start.getRGB() >> 16 & 0xFF) / 255.0f;
            final float startG= (start.getRGB() >> 8 & 0xFF) / 255.0f;
            final float startB = (start.getRGB() & 0xFF) / 255.0f;

            final float endA = (end.getRGB() >> 24 & 0xFF) / 255.0f;
            final float endR = (end.getRGB() >> 16 & 0xFF) / 255.0f;
            final float endG = (end.getRGB() >> 8 & 0xFF) / 255.0f;
            final float endB = (end.getRGB() & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2d(vec.x1, vec.y1);
                GL11.glVertex2d(vec.x2, vec.y2);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2d(vec.x3, vec.y3);
                GL11.glVertex2d(vec.x4, vec.y4);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else {
            final float startA = (start.getRGB() >> 24 & 0xFF) / 255.0f;
            final float startR = (start.getRGB() >> 16 & 0xFF) / 255.0f;
            final float startG= (start.getRGB() >> 8 & 0xFF) / 255.0f;
            final float startB = (start.getRGB() & 0xFF) / 255.0f;

            final float endA = (end.getRGB() >> 24 & 0xFF) / 255.0f;
            final float endR = (end.getRGB() >> 16 & 0xFF) / 255.0f;
            final float endG = (end.getRGB() >> 8 & 0xFF) / 255.0f;
            final float endB = (end.getRGB() & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2d(vec.x1, vec.y1);
                GL11.glVertex2d(vec.x4, vec.y4);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2d(vec.x3, vec.y3);
                GL11.glVertex2d(vec.x2, vec.y2);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }
}
