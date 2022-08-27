package com.kisman.cc.util.render.cubic;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Cubic's GL Abstraction
 * Simplifies usage of common GL code and
 * makes working in GL easier
 *
 * @author Cubic
 * @since 25.08.2022
 */
@Deprecated
public class CGLA {

    public static final Tessellator tessellator = Tessellator.getInstance();

    public static final BufferBuilder buf = tessellator.getBuffer();

    public static void quad2D(Vertex2D<Double> v1, Vertex2D<Double> v2, Vertex2D<Double> v3, Vertex2D<Double> v4){
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertex2D(v1);
        vertex2D(v2);
        vertex2D(v3);
        vertex2D(v4);
        tessellator.draw();
    }

    public static void rect(Vertex2D<Double> v1, Vertex2D<Double> v2){
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertex2D(v1);
        vertex2D(genVertex2D(v1.x, v2.y, v1.color));
        vertex2D(v2);
        vertex2D(genVertex2D(v2.x, v1.y, v1.color));
        tessellator.draw();
    }

    public static void setup(){
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
    }

    public static void release(){
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void prepare(){
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL_SMOOTH);
    }

    public static void restore(){
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL_FLAT);
    }

    public static <T extends Number> void vertex2D(Vertex2D<T> vertex){
        buf.pos((Double) vertex.getX(), (Double) vertex.getY(), 0.0).color(vertex.getColor().getRed(), vertex.getColor().getGreen(), vertex.getColor().getBlue(), vertex.getColor().getAlpha());
    }

    public static <T extends Number> void vertex3D(Vertex3D<T> vertex){
        buf.pos((Double) vertex.getX(), (Double) vertex.getY(), (Double) vertex.getZ()).color(vertex.getColor().getRed(), vertex.getColor().getGreen(), vertex.getColor().getBlue(), vertex.getColor().getAlpha());
    }

    public static Color color(int r, int g, int b){
        return new Color(r, g, b);
    }

    public static Color color(float r, float g, float b){
        return new Color((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f));
    }

    public static Color color(int r, int g, int b, int a){
        return new Color(r, g, b, a);
    }

    public static Color color(float r, float g, float b, float a){
        return new Color((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f));
    }

    public static <T extends Number> Vertex2D<T> genVertex2D(T x, T y, Color color){
        return new Vertex2D<>(x, y, color);
    }

    public static <T extends Number> Vertex3D<T> genVertex3D(T x, T y, T z, Color color){
        return new Vertex3D<>(x, y, z, color);
    }

    public static final class Vertex2D<T extends Number> {

        private final T x;

        private final T y;

        private final Color color;

        public Vertex2D(T x, T y, Color color){
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public T getX() {
            return x;
        }

        public T getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }
    }

    public static final class Vertex3D<T extends Number> {

        private final T x;

        private final T y;

        private final T z;

        private final Color color;

        public Vertex3D(T x, T y, T z, Color color){
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }

        public T getX() {
            return x;
        }

        public T getY() {
            return y;
        }

        public T getZ() {
            return z;
        }

        public Color getColor() {
            return color;
        }
    }
}
