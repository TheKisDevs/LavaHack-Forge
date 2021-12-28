package com.kisman.cc.oldclickgui.csgo;

import com.kisman.cc.util.GLUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.glClearDepthf;

public class ClientBaseRendererImpl implements IRenderer {
    @Override public void drawRect(double x, double y, double w, double h, Color c) {GLUtil.drawRect(GL_QUADS, (int) x / 2, (int) y / 2, (int) x / 2 + (int)  w / 2, (int) y / 2 + (int) h / 2, ColorUtils.getColor(c));}

    @Override
    public void drawOutline(double x, double y, double w, double h, float lineWidth, Color c) {
        glLineWidth(lineWidth);
        GLUtil.drawRect(GL_LINE_LOOP, (int) x / 2, (int) y / 2, (int) x / 2 + (int) w / 2, (int) y / 2 + (int) h / 2, ColorUtils.getColor(c));
    }

    @Override public void setColor(Color c) {ColorUtils.glColor(c);}
    @Override public void drawString(int x, int y, String text, Color color) {CustomFontUtil.drawString(text, x / 2f, y / 2f + 1, ColorUtils.getColor(color), true);}
    @Override public int getStringWidth(String str) {return CustomFontUtil.getStringWidth(str) * 2;}
    @Override public int getStringHeight(String str) {return CustomFontUtil.getFontHeight(true) * 2;}
    @Override public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {}

    @Override
    public void initMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    @Override
    public void useMask() {
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glDepthFunc(GL_EQUAL);
    }

    @Override
    public void disableMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
    }

    @Override public int astolfoColor() {return ColorUtils.astolfoColors(100, 100);}
    @Override public Color astolfoColorToObj() {return ColorUtils.astolfoColorsToColorObj(100, 100);}
}