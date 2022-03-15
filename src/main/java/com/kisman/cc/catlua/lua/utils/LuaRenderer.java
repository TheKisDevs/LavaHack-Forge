package com.kisman.cc.catlua.lua.utils;

import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.sun.javafx.geom.Vec2d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class LuaRenderer implements Globals {
    private static LuaRenderer instance;

    LuaRenderer() {
    }

    public void text(String text, Vec2d vec2d, Color color) {
        CustomFontUtil.drawStringWithShadow(text, vec2d.x, vec2d.y, color.getRGB());
    }

    public void rect(Vec2d from, Vec2d to, Color color) {
        Render2DUtil.drawRect(from.x, from.y, to.x, to.y, color.getRGB());
    }

    public void line(Vec2d from, Vec2d to, Color color) {

    }

    public int width(String text) {
        return CustomFontUtil.getStringWidth(text);
    }

    public double windowWidth() {
        return sr.getScaledWidth_double();
    }

    public double windowHeight() {
        return sr.getScaledHeight_double();
    }

    //3D

    public void drawBoxESP(LuaBox box, Color c) {
        RenderUtil.drawBoxESP(box.toAABB(), c, 1, true, true, 100, 255);
    }

    public void drawBlockESP(BlockPos pos, Color c) {
        Colour color = new Colour(c);
        RenderUtil.drawBlockESP(pos, color.r1, color.g1, color.b1);
    }

    public void setup() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void setup3D() {
        setup();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
    }

    public void clean() {
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public void clean3D() {
        clean();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
    }

    public static LuaRenderer getDefault() {
        if(instance == null) instance = new LuaRenderer();
        return instance;
    }
}
