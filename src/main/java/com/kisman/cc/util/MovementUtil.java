package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Objects;

public class MovementUtil {
    public static final double WALK_SPEED = 0.221;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + 2.5, mc.player.posZ - 0.3);
        return !MovementUtil.mc.world.getCollisionBoxes(mc.player, bb).isEmpty();
    }

    public static double getSpeed() {return getSpeed(false, 0.2873);}

    public static double getSpeed(boolean slowness, double defaultSpeed) {
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0;

        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            //noinspection ConstantConditions
            int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1;
        }

        return defaultSpeed;
    }

    public static double getDistance2D() {
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static void strafe(float speed) {
        if (!isMoving()) return;
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * (double)speed;
        mc.player.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static boolean isMoving() {
        if (mc.player == null) return false;
        if (mc.player.movementInput.moveForward != 0.0f) return true;
        return mc.player.movementInput.moveStrafe != 0.0f;
    }

    public static float getDirection() {
        float var1 = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) var1 += 180.0f;

        float forward = 1.0f;

        if (mc.player.moveForward < 0.0f) forward = -0.5f;
        else if (mc.player.moveForward > 0.0f) forward = 0.5f;
        if (mc.player.moveStrafing > 0.0f) var1 -= 90.0f * forward;
        if (mc.player.moveStrafing < 0.0f) var1 += 90.0f * forward;

        return var1 *= (float)Math.PI / 180;
    }
}