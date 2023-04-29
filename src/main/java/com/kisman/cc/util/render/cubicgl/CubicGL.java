package com.kisman.cc.util.render.cubicgl;

import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.cubic.dynamictask.AbstractTask;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.kisman.cc.util.Globals.mc;

public class CubicGL {

    public static final int CGL_SHADE_MODEL_FLAT = 0;
    public static final int CGL_SHADE_MODEL_SMOOTH = 1;

    public static void scissors(int x1, int y1, int x2, int y2){
        int mx = Math.min(x1, x2);
        int my = Math.min(y1, y2);
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        GL11.glScissor(mx, my, dx, dy);
    }

    public static void scissorsNormalized(double x1, double y1, double x2, double y2){
        scissors((int) (x1 * Display.getWidth()), (int) (y1 * Display.getHeight()), (int) (x2 * Display.getWidth()), (int) (y2 * Display.getHeight()));
    }

    public static void scissorNormalizedCenter(double x1, double y1, double x2, double y2){
        scissorsNormalized((x1 + 1.0) / 2.0, (y1 + 1.0) / 2.0, (x2 + 1.0) / 2.0, (y2 + 1.0) / 2.0);
    }

    public static void setup(){
        Rendering.start();
    }

    public static void prepare(){
        Rendering.start();
    }

    public static void restore(){
        Rendering.end();
    }

    public static void release(){
        Rendering.end();
    }

    public static void drawRect(int x1, int y1, int x2, int y2, Color color){
        drawRect(CGL_SHADE_MODEL_FLAT, DisplayVertex2D.of(x1, y1, x2, y2), color);
    }

    public static void drawRect(int shadeModel, int x1, int y1, int x2, int y2, Color color){
        drawRect(shadeModel, DisplayVertex2D.of(x1, y1, x2, y2), color);
    }

    public static void drawRect(DisplayVertex2D vertex2D, Color color){
        drawRect(CGL_SHADE_MODEL_FLAT, vertex2D, color);
    }

    public static void drawRect(int shadeModel, DisplayVertex2D vertex2D, Color color){
        run("shadeModelBegin", shadeModel);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buf.pos(vertex2D.getX1(), vertex2D.getY1(), 0);
        buf.pos(vertex2D.getX2(), vertex2D.getY1(), 0);
        buf.pos(vertex2D.getX2(), vertex2D.getY2(), 0);
        buf.pos(vertex2D.getX1(), vertex2D.getY2(), 0);
        tessellator.draw();
        run("shadeModelEnd", shadeModel);
    }

    public static void scale(double x, double y, double w, double h, double scale) {
        ScaledResolution sr = new ScaledResolution(mc);

        GL11.glScaled((sr.getScaledWidth() * scale) / 2.0 - (w * scale) / 2.0, (sr.getScaledHeight() * scale) / 2.0 - (h * scale) / 2.0, 0.0);
        GL11.glScaled(scale, scale, scale);
//        GL11.glTranslated(x - (w * scale) / 2 + sr.getScaledWidth() / (2.0 * scale), y - (h * scale) / 2 + sr.getScaledHeight() / (2.0 * scale), 0);
//        GL11.glTranslated(x + (w * scale) / 2, y + (h * scale) / 2, 0.0);
//        GL11.glTranslated(-(w * scale) / 2.0, (h * scale) / 2.0, 0.0);
//        GL11.glScaled(sr.getScaledWidth() / 2.0 - w / 2.0, sr.getScaledHeight() / 2.0 - h / 2.0, 0.0);
    }

    private static final Map<String, Map<Integer, AbstractTask<Integer>>> tasks = new ConcurrentHashMap<>();

    private static int run(String type, Integer mode, Object... args){
        return tasks.get(type).get(mode).doTask(args);
    }

    static {
        Map<Integer, AbstractTask<Integer>> shadeModelBeginMap = new ConcurrentHashMap<>();
        shadeModelBeginMap.put(CGL_SHADE_MODEL_FLAT, AbstractTask.types(Integer.class).task(arg -> {
            setup();
            return 0;
        }));
        shadeModelBeginMap.put(CGL_SHADE_MODEL_SMOOTH, AbstractTask.types(Integer.class).task(arg -> {
            setup();
            prepare();
            return 0;
        }));
        tasks.put("shadeModelBegin", shadeModelBeginMap);
        Map<Integer, AbstractTask<Integer>> shadeModelEndMap = new ConcurrentHashMap<>();
        shadeModelEndMap.put(CGL_SHADE_MODEL_FLAT, AbstractTask.types(Integer.class).task(arg -> {
            release();
            return 0;
        }));
        shadeModelEndMap.put(CGL_SHADE_MODEL_SMOOTH, AbstractTask.types(Integer.class).task(arg -> {
            restore();
            release();
            return 0;
        }));
        tasks.put("shadeModelEnd", shadeModelEndMap);
    }
}
