package com.kisman.cc.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MathUtil {

    public static double degToRad(double deg) {
        return deg * (float) (Math.PI / 180.0f);
    }

    /**
     * @author DarkStorm
     */
    public static Point calculateMouseLocation() {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = mc.gameSettings.guiScale;
        if (scale == 0) scale = 1000;
        int scaleFactor = 0;
        while (scaleFactor < scale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) scaleFactor++;
        return new Point(Mouse.getX() / scaleFactor, mc.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks) {
        return interpolateEntity(entity, ticks).subtract(Minecraft.getMinecraft().getRenderManager().viewerPosX, Minecraft.getMinecraft().getRenderManager().viewerPosY, Minecraft.getMinecraft().getRenderManager().viewerPosZ);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static Vec3d mult(Vec3d factor, float multiplier) {
        return new Vec3d(factor.x * multiplier, factor.y * multiplier, factor.z * multiplier);
    }

    public static Vec3d div(Vec3d factor, float divisor) {
        return new Vec3d(factor.x / divisor, factor.y / divisor, factor.z / divisor);
    }

    public static double[] directionSpeedNoForward(double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = 1f;

        if (mc.gameSettings.keyBindLeft.isPressed() || mc.gameSettings.keyBindRight.isPressed() || mc.gameSettings.keyBindBack.isPressed() || mc.gameSettings.keyBindForward.isPressed()) forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0) yaw += (forward > 0 ? -45 : 45);
            else if (side < 0) yaw += (forward > 0 ? 45 : -45);
            side = 0;
            // forward = clamp(forward, 0, 1);
            if (forward > 0) forward = 1;
            else if (forward < 0) forward = -1;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.floatValue();
    }

    public static double[] directionSpeed(double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double distance(float x, float y, float x1, float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }
    
    public static double lerp(double from, double to, double delta) {
        return from + (to - from) * delta;
    }

    public static List<BlockPos> getBlocksForLine(double z, double x1, double y1, double x2, double y2){
        List<BlockPos> blocks = new ArrayList<>();
        double x = x1;
        double y = y1;
        double dX = Math.abs(x2 - x1);
        double dY = Math.abs(y2 - y1);
        double s1 = Math.signum(x2 - x1);
        double s2 = Math.signum(y2 - y1);
        double interchange;
        double t;
        if(dY > dX){
            t = dX;
            dX = dY;
            dY = t;
            interchange = 1;
        } else {
            interchange = 0;
        }
        double e = 2 * dY - dX;
        double a = 2 * dY;
        double b = 2 * dY - 2 * dX;
        for(int i = 0; i < dX; i++){
            if(e < 0){
                if(interchange == 1){
                    y = y + s2;
                } else {
                    x = x + x1;
                }
                e = e + a;
            } else {
                y = y + s2;
                x = x + s1;
                e = e + b;
            }
            blocks.add(new BlockPos(x, y, z));
        }
        return blocks;
    }

    public static double curve(double a){
        double x = a - 1.0;
        return Math.sqrt(1.0 - (x * x));
    }

    public static double curve2(double a){
        return Math.sqrt(-(a * a) - 2 * a);
    }

    public static double smoothstep(double a){
        return (a * a) + ((1.0 - (1.0 - a) * (1.0 - a)) - (a * a)) * a;
    }

    public static double normalize(double a, double bound){
        return a % bound;
    }

    public static double absNormalize(double a, double bound){
        return (a % bound + bound) % bound;
    }

    public static float normalize(float a, float bound){
        return a % bound;
    }

    public static float absNormalize(float a, float bound){
        return (a % bound + bound) % bound;
    }
}
