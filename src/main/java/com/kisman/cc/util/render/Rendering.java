package com.kisman.cc.util.render;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.client.collections.Triple;
import com.kisman.cc.util.enums.DirectionVertexes;
import com.kisman.cc.util.render.cubic.BoundingBox;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static org.lwjgl.opengl.GL11.*;

/**
 * Is not skidded class
 * _kisman_ wanted this  - Cubic
 *
 * ONG ONG ONG kisman UNSKIDDED IT FR????? - Cubic
 */
@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
public class Rendering {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Tessellator tessellator = Tessellator.getInstance();

    public static BufferBuilder bufferbuilder = tessellator.getBuffer();

    public static final Colour DUMMY_COLOR = new Colour(0, 0, 0, 0);

    public enum RenderObject {
        BOX {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, boolean partial, ArrayList<DirectionVertexes> sides, Object... values) {
                if(gradient) {
                    drawGradientFilledBox(aabb, color1, color2);
                } else {
                    if(partial) drawPartialSelectionBox(aabb, color1, sides);
                    else drawSelectionBox(aabb, color1);
                }
            }
        },
        OUTLINE {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, boolean partial, ArrayList<DirectionVertexes> sides, Object... values) {
                if(gradient) {
                    drawGradientBlockOutline(aabb, color1, color2, (float) values[0]);
                } else {
                    glLineWidth((float) values[0]);
                    if(partial) drawPartialSelectionBoundingBox(aabb, color1, sides);
                    else drawSelectionBoundingBox(aabb, color1);
                }
            }
        },
        WIRE {
            @Override
            void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, boolean partial, ArrayList<DirectionVertexes> sides, Object... values) {
                drawWire(aabb, (float) values[0], color1, gradient ? color2 : color1);
                /*glPushMatrix();

                OutlineUtils.setColor(color1);
                OutlineUtils.renderOne((float) values[0]);

                drawDummyBox(aabb);

                OutlineUtils.renderTwo();

                drawDummyBox(aabb);

                OutlineUtils.renderThree();
                OutlineUtils.renderFour(Color.WHITE.getRGB()*//*color2.getRGB()*//*, 1f);
                OutlineUtils.setColor(gradient ? color2 : color1);

                drawDummyBox(aabb);

                OutlineUtils.renderFive(-1f);
                OutlineUtils.setColor(Color.WHITE);

                glPopMatrix();*/
            }

            private void drawDummyBox(AxisAlignedBB aabb) {
                drawSelectionBox(aabb, DUMMY_COLOR.getColor());
            }
        }

        ;

        abstract void draw(AxisAlignedBB aabb, Color color1, Color color2, boolean gradient, boolean partial, ArrayList<DirectionVertexes> sides, Object... values);
        
        public void draw(AxisAlignedBB aabb, Color color, Object... values) {
            draw(aabb, color, color, false, false, new ArrayList<>(), values);
        }
        
        public void draw(AxisAlignedBB aabb, Color color1, Color color2, Object... values) {
            draw(aabb, color1, color2, true, false, new ArrayList<>(), values);
        }
    }

    public enum Mode {
        OUTLINE(false, RenderObject.OUTLINE),
        BOX(false, RenderObject.BOX),
        WIRE(false, RenderObject.WIRE),
        BOX_OUTLINE(false, RenderObject.BOX, RenderObject.OUTLINE),
        BOX_WIRE(false, RenderObject.BOX, RenderObject.WIRE),
        WIRE_OUTLINE(false, RenderObject.OUTLINE, RenderObject.WIRE),
        BOX_WIRE_OUTLINE(false, RenderObject.BOX, RenderObject.OUTLINE, RenderObject.WIRE),
        WIRE_GRADIENT(true, RenderObject.WIRE),
        BOX_GRADIENT(true, RenderObject.BOX),
        OUTLINE_GRADIENT(true, RenderObject.OUTLINE),
        BOX_OUTLINE_GRADIENT(true, RenderObject.BOX, RenderObject.OUTLINE),
        BOX_WIRE_GRADIENT(true, RenderObject.BOX, RenderObject.WIRE),
        WIRE_OUTLINE_GRADIENT(true, RenderObject.OUTLINE, RenderObject.WIRE),
        BOX_WIRE_OUTLINE_GRADIENT(true, RenderObject.BOX, RenderObject.OUTLINE, RenderObject.WIRE, RenderObject.BOX)

        ;

        public final ArrayList<RenderObject> objects;
        public final boolean gradient;

        Mode(boolean gradient, RenderObject... objects) {
            this.gradient = gradient;
            this.objects = new ArrayList<>(Arrays.asList(objects));
        }

        public void draw(AxisAlignedBB aabb, Color filledColor1, Color filledColor2, Color outlineColor1, Color outlineColor2, Color wireColor1, Color wireColor2, boolean depth, boolean partial, ArrayList<DirectionVertexes> sides, Object... values) {
            for(RenderObject object : objects) {
                if(object == RenderObject.WIRE) object.draw(aabb, wireColor1, wireColor2, gradient, partial, sides, values);
                else {
                    start(depth);

                    if(gradient) prepare();

                    if (object == RenderObject.BOX) object.draw(aabb, filledColor1, filledColor2, gradient, partial, sides, values);
                    else if (object == RenderObject.OUTLINE) object.draw(aabb, outlineColor1, outlineColor2, gradient, partial, sides, values);

                    if(gradient) restore();

                    end(depth);
                }
            }
        }
    }

    public static void start(boolean depth) {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        if(!depth) glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glEnable(GL_CULL_FACE);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        glDisable(GL_LIGHTING);
        glLineWidth(1.5f);
    }

    public static void start() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glEnable(GL_CULL_FACE);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        glDisable(GL_LIGHTING);
        glLineWidth(1.5f);
    }
    public static void end() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void end(boolean depth) {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if(!depth) GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void setup(boolean depth) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        if(!depth) GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
    }

    public static void setup() {
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

    public static void release(boolean depth){
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        if(!depth) GlStateManager.enableDepth();
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

    public static void drawWire(AxisAlignedBB bb, float lineWidth, Color color1, Color color2) {
        Vec3d point1 = new Vec3d(bb.minX, bb.minY, bb.minZ);
        Vec3d point2 = new Vec3d(bb.minX, bb.maxY, bb.minZ);
        Vec3d point3 = new Vec3d(bb.minX, bb.maxY, bb.maxZ);
        Vec3d point4 = new Vec3d(bb.minX, bb.minY, bb.maxZ);
        Vec3d point5 = new Vec3d(bb.maxX, bb.minY, bb.minZ);
        Vec3d point6 = new Vec3d(bb.maxX, bb.maxY, bb.minZ);
        Vec3d point7 = new Vec3d(bb.maxX, bb.minY, bb.maxZ);
        Vec3d point8 = new Vec3d(bb.maxX, bb.minY, bb.maxZ);

        double distance1 = UtilityKt.distanceSq(point1);
        double distance2 = UtilityKt.distanceSq(point2);
        double distance3 = UtilityKt.distanceSq(point3);
        double distance4 = UtilityKt.distanceSq(point4);
        double distance5 = UtilityKt.distanceSq(point5);
        double distance6 = UtilityKt.distanceSq(point6);
        double distance7 = UtilityKt.distanceSq(point7);
        double distance8 = UtilityKt.distanceSq(point8);

        HashMap<Double, Vec3d> distancePairs = new HashMap<>();
        HashMap<Vec3d, Color> colorPairs = new HashMap<>();
        HashSet<Vec3d> vertexes = new HashSet<>();

        distancePairs.put(distance1, point1);
        distancePairs.put(distance2, point2);
        distancePairs.put(distance3, point3);
        distancePairs.put(distance4, point4);
        distancePairs.put(distance5, point5);
        distancePairs.put(distance6, point6);
        distancePairs.put(distance7, point7);
        distancePairs.put(distance8, point8);

        colorPairs.put(point1, color2);
        colorPairs.put(point2, color1);
        colorPairs.put(point3, color1);
        colorPairs.put(point4, color2);
        colorPairs.put(point5, color2);
        colorPairs.put(point6, color1);
        colorPairs.put(point7, color2);
        colorPairs.put(point8, color1);

        vertexes.addAll(distancePairs.values());

        double maxDistance = Double.MIN_VALUE;
        double minDistance = Double.MAX_VALUE;

        for(double distance : distancePairs.keySet()) {
            if(distance < minDistance) minDistance = distance;
            if(distance > maxDistance) maxDistance = distance;
        }

        vertexes.remove(distancePairs.get(maxDistance));
        vertexes.remove(distancePairs.get(minDistance));

        glLineWidth(lineWidth);

        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for(Vec3d vec : vertexes) {
            Color color = colorPairs.get(vec);

            bufferbuilder.pos(vec.x, vec.y, vec.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }

        tessellator.draw();
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Mode mode) {
        draw0(axisAlignedBB, lineWidth, c, c1, mode, false);
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Mode mode, boolean depth) {
        draw0(axisAlignedBB, lineWidth, c, c1, c.withAlpha(255), c1.withAlpha(255), c.withAlpha(255), c1.withAlpha(255), mode, depth);
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Colour c2, Colour c3, Colour c4, Colour c5, Mode mode) {
        draw0(axisAlignedBB, lineWidth, c, c1, c2, c3, c4, c5, mode, false);
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Colour c2, Colour c3, Colour c4, Colour c5, Mode mode, boolean depth) {
        draw0(axisAlignedBB, lineWidth, c, c1, c2, c3, c4, c5, mode, depth, false, new ArrayList());
    }

    public static void draw0(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Colour c2, Colour c3, Colour c4, Colour c5, Mode mode, boolean depth, boolean partial, ArrayList<DirectionVertexes> sides) {
        Color filledColor1 = c.getColor();
        Color filledColor2 = c1.getColor();
        Color outlineColor1 = c2.getColor();
        Color outlineColor2 = c3.getColor();
        Color wireColor1 = c4.getColor();
        Color wireColor2 = c5.getColor();

        mode.draw(axisAlignedBB, filledColor1, filledColor2, outlineColor1, outlineColor2, wireColor1, wireColor2, depth, partial, sides, lineWidth);
    }

    public static void draw(AxisAlignedBB axisAlignedBB, float lineWidth, Colour c, Colour c1, Mode mode){
        draw0(axisAlignedBB, lineWidth, c, c1, mode);
    }

    public static AxisAlignedBB correct(AxisAlignedBB aabb){
        return new AxisAlignedBB(aabb.minX - mc.getRenderManager().viewerPosX, aabb.minY - mc.getRenderManager().viewerPosY, aabb.minZ - mc.getRenderManager().viewerPosZ, aabb.maxX - mc.getRenderManager().viewerPosX, aabb.maxY - mc.getRenderManager().viewerPosY, aabb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    // possible not working
    public static BlockPos correct(BlockPos pos){
        AxisAlignedBB abb = correct(new AxisAlignedBB(pos));
        return new BlockPos(abb.minX, abb.minY, abb.minZ);
    }

    public static AxisAlignedBB scale(BlockPos pos, double scale){
        double s = scale * 0.5;
        double x1 = (pos.getX() + 0.5) - s;
        double y1 = (pos.getY() + 0.5) - s;
        double z1 = (pos.getZ() + 0.5) - s;
        double x2 = (pos.getX() + 0.5) + s;
        double y2 = (pos.getY() + 0.5) + s;
        double z2 = (pos.getZ() + 0.5) + s;
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static AxisAlignedBB scale(AxisAlignedBB bb, double scale) {
        double s = scale * 0.5;
        return new AxisAlignedBB(
                bb.minX - s,
                bb.minY - s,
                bb.minZ - s,
                bb.maxX + s,
                bb.maxY + s,
                bb.maxZ + s
        );
    }

    public static BoundingBox animateMove(BoundingBox origin, BoundingBox destination, float partialTicks, float lengthPartialTicks){
        float m = partialTicks / lengthPartialTicks;
        double maxX = origin.maxX + ((destination.maxX - origin.maxX) * m) + (((destination.maxX - destination.minX) - (origin.maxX - origin.minX)) * 0.5 * m);
        double maxY = origin.maxY + ((destination.maxY - origin.maxY) * m) + (((destination.maxY - destination.minY) - (origin.maxY - origin.minY)) * 0.5 * m);
        double maxZ = origin.maxZ + ((destination.maxZ - origin.maxZ) * m) + (((destination.maxZ - destination.minZ) - (origin.maxZ - origin.minZ)) * 0.5 * m);
        double minX = origin.minX + ((destination.maxX - origin.maxX) * m) + (((destination.maxX - destination.minX) - (origin.maxX - origin.minX)) * 0.5 * m);
        double minY = origin.minY + ((destination.maxY - origin.maxY) * m) + (((destination.maxY - destination.minY) - (origin.maxY - origin.minY)) * 0.5 * m);
        double minZ = origin.minZ + ((destination.maxZ - origin.maxZ) * m) + (((destination.maxZ - destination.minZ) - (origin.maxZ - origin.minZ)) * 0.5 * m);
        return new BoundingBox(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static void drawTripleGradient(AxisAlignedBB aabb, Colour colour1, Colour colour2, Colour colour3){
        double yAdd = (aabb.maxY - aabb.minY) * 0.5;
        AxisAlignedBB aabb1 = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + yAdd, aabb.maxZ);
        AxisAlignedBB aabb2 = new AxisAlignedBB(aabb.minX, aabb.minY + yAdd, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        start();
        drawGradientFilledBox(aabb1, colour1.getColor(), colour2.getColor());
        drawGradientFilledBox(aabb2, colour2.getColor(), colour3.getColor());
        end();
    }

    public static void drawTripleGradient2(AxisAlignedBB aabb, Colour colour1, Colour colour2, Colour colour3){
        double yAdd = (aabb.maxY - aabb.minY) * 0.5;
        AxisAlignedBB aabb1 = new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + yAdd, aabb.maxZ);
        AxisAlignedBB aabb2 = new AxisAlignedBB(aabb.minX, aabb.minY + yAdd, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        start();
        drawTopOpenGradientBox(aabb2, colour1.getColor(), colour2.getColor());
        drawBottomOpenedGradientBox(aabb1, colour2.getColor(), colour3.getColor());
        end();
    }

    public static void drawTopOpenGradientBox(AxisAlignedBB bb, Color startColor, Color endColor){
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawBottomOpenedGradientBox(AxisAlignedBB bb, Color startColor, Color endColor){
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        //bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        //bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawSelectionBox(AxisAlignedBB axisAlignedBB, Color color) {
        bufferbuilder.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color);
        tessellator.draw();
    }

    public static void drawPartialSelectionBox(AxisAlignedBB bb, Color color, ArrayList<DirectionVertexes> sides) {
        bufferbuilder.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addPartialVertexes(bufferbuilder, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color, sides);
        tessellator.draw();
    }

    public static void addPartialVertexes(BufferBuilder builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color, ArrayList<DirectionVertexes> sides) {
        for(DirectionVertexes side : sides) {
            for(Triple<DirectionVertexes.AxisValues> vertex : side.vertexes) {
                builder.pos(
                        side.valueOf(vertex, DirectionVertexes.Directions.X, minX, maxX),
                        side.valueOf(vertex, DirectionVertexes.Directions.Y, minY, maxY),
                        side.valueOf(vertex, DirectionVertexes.Directions.Z, minZ, maxZ)
                ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            }
        }
    }

    public static void addChainedFilledBoxVertices(BufferBuilder builder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB axisAlignedBB, Color color) {
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedBoundingBoxVertices(bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color);
        tessellator.draw();
    }

    public static void drawPartialSelectionBoundingBox(AxisAlignedBB axisAlignedBB, Color color, ArrayList<DirectionVertexes> sides) {
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addPartialVertexes(bufferbuilder, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, sides);
        tessellator.draw();
    }

    public static void addChainedBoundingBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
    }

    public static void drawGradientFilledBox(AxisAlignedBB bb, Color startColor, Color endColor) {
        float alpha = (float) endColor.getAlpha() / 255.0f;
        float red = (float) endColor.getRed() / 255.0f;
        float green = (float) endColor.getGreen() / 255.0f;
        float blue = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) startColor.getRed() / 255.0f;
        float green1 = (float) startColor.getGreen() / 255.0f;
        float blue1 = (float) startColor.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red1 = (float)endColor.getRed() / 255.0f;
        float green1 = (float)endColor.getGreen() / 255.0f;
        float blue1 = (float)endColor.getBlue() / 255.0f;
        float alpha1 = (float)endColor.getAlpha() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    /**
     * @author Cubic
     *
     * Work in progress
     */
    public static void drawTripleGradientBox(AxisAlignedBB bb, Color c1, Color c2, Color c3){
        double dY = (bb.maxY - bb.minY) / 2.0;
        AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + dY, bb.maxZ);
        AxisAlignedBB bb2 = new AxisAlignedBB(bb.minX, bb.minY + dY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        int red1 = c1.getRed();
        int green1 = c1.getGreen();
        int blue1 = c1.getBlue();
        int alpha1 = c1.getAlpha();
        int red2 = c2.getRed();
        int green2 = c2.getGreen();
        int blue2 = c2.getBlue();
        int alpha2 = c2.getAlpha();
        int red3 = c3.getRed();
        int green3 = c3.getGreen();
        int blue3 = c3.getBlue();
        int alpha3 = c3.getAlpha();
        start();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.minX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.minX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb1.maxX, bb1.minY, bb1.minZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.minY, bb1.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb1.maxX, bb1.maxY, bb1.minZ).color(red2, green2, blue2, alpha2).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();

        buf.pos(bb2.minX, bb2.minY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();
        buf.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red3, green3, blue3, alpha3).endVertex();

        tessellator.draw();
        end();
    }

    /**
     * @author Cubic
     */
    public static void drawChrome(AxisAlignedBB bb, EnumFacing facing, Color c1, Color c2, Color c3, Color c4){
        start();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        switch(facing){
            case UP:
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case DOWN:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case NORTH:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case EAST:
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case SOUTH:
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
            case WEST:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();
                break;
        }
        tessellator.draw();
        end();
    }

    /**
     * WIP
     * @author Cubic
     */
    public static void drawChromaOutline(AxisAlignedBB bb, EnumFacing facing, float lineWidth, Color c1, Color c2, Color c3, Color c4){
        start();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        GL11.glLineWidth(lineWidth);
        buf.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        switch(facing){
            case UP:
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case DOWN:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case NORTH:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case SOUTH:
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case EAST:
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
            case WEST:
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());

                buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha());
                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());

                buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha());
                buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                break;
        }
        tessellator.draw();
        end();
    }

    public static void drawChromaBox(AxisAlignedBB bb, Color c1, Color c2, Color c3, Color c4){
        start();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Up
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // Down
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // North
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // South
        buf.pos(bb.maxX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // East
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        // West
        buf.pos(bb.minX, bb.minY, bb.minZ).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.minY, bb.maxZ).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.maxZ).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        buf.pos(bb.minX, bb.maxY, bb.minZ).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c3.getAlpha()).endVertex();

        tessellator.draw();
        end();
    }

    public static void drawBoxESP(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks) {
        try {
            double renderPosX = mc.renderManager.viewerPosX;
            double renderPosY = mc.renderManager.viewerPosY;
            double renderPosZ = mc.renderManager.viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)  + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = mc.renderManager.playerViewY;
            float playerViewX = mc.renderManager.playerViewX;
            boolean thirdPersonView = mc.renderManager.options.thirdPersonView == 2;

            start();
            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(0, 1, 0.0);
            GL11.glVertex3d(-0.5, 0.5, 0.0);
            GL11.glVertex3d(0, 1, 0.0);
            GL11.glVertex3d(0.5, 0.5, 0.0);

            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d(-0.5, 0.5, 0.0);
            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d(0.5, 0.5, 0.0);

            GL11.glEnd();
            end();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static class TextRendering {
        public static void drawText(BlockPos pos, String text, int color) {
            GlStateManager.pushMatrix();
            glBillboardDistanceScaled(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, mc.player, 1.0f);
            GlStateManager.disableDepth();
            GlStateManager.translate(-(CustomFontUtil.getStringWidth(text) / 2.0), 0.0, 0.0);
            CustomFontUtil.drawStringWithShadow(text, 0.0f, 0.0f, color);
            GlStateManager.popMatrix();
        }

        public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
            glBillboard(x, y, z);
            int distance = (int) player.getDistance(x, y, z);
            float scaleDistance = distance / 2.0f / (2.0f + (2.0f - scale));
            if (scaleDistance < 1.0f) scaleDistance = 1.0f;
            GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
        }

        public static void glBillboard(float x, float y, float z) {
            float scale = 0.02666667f;
            GlStateManager.translate(x - mc.
                    renderManager.
                    renderViewEntity.
                    posX,
                    y - mc.renderManager.renderViewEntity.posY, z - mc.renderManager.renderViewEntity.posZ);
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.player.rotationPitch, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-scale, -scale, scale);
        }
    }
}
