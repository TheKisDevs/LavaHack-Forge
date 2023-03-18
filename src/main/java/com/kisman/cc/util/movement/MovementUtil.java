package com.kisman.cc.util.movement;

import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.math.MathKt;
import com.kisman.cc.util.math.TrigonometryKt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

import java.util.Objects;

public class MovementUtil {
    public static final double WALK_SPEED = 0.221;
    public static final double DEFAULT_SPEED = 0.2873;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static double getJumpHeight(boolean strict) {
        double jumpHeight = strict ? 0.42 : 0.3995;
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) jumpHeight += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
        return jumpHeight;
    }

    public static void setMotion(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static double[] forward(final double speed) {
        float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) yaw += ((forward > 0.0f) ? -45 : 45);
            else if (side < 0.0f) yaw += ((forward > 0.0f) ? 45 : -45);
            side = 0.0f;
            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = .2873;
        if (mc.player != null && mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getSpeed() {return getSpeed(false, DEFAULT_SPEED);}

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

    public static void strafe(float yaw, double speed) {
        if (!isMoving()) return;
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
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
        float yaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) yaw += 180.0f;

        float forward = 1.0f;

        if (mc.player.moveForward < 0.0f) forward = -0.5f;
        else if (mc.player.moveForward > 0.0f) forward = 0.5f;
        if (mc.player.moveStrafing > 0.0f) yaw -= 90.0f * forward;
        if (mc.player.moveStrafing < 0.0f) yaw += 90.0f * forward;

        return yaw * ((float) Math.PI / 180);
    }

    public static void hClip(double off) {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        mc.player.setPosition(mc.player.posX + (-Math.sin(yaw) * off), mc.player.posY, mc.player.posZ + (Math.cos(yaw) * off));
    }

    private static float getRoundedMovementInput(float input) {
        return (input > 0.0F) ? 1.0F : ((input < 0.0F) ? -1.0F : 0.0F);
    }

    public static double[] strafe(double speed) {
        return strafe(mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed) {
        return strafe(entity, mc.player.movementInput, speed);
    }

    public static double[] strafe(Entity entity, MovementInput movementInput, double speed) {
        float moveForward = movementInput.moveForward;
        float moveStrafe  = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.getRenderPartialTicks();

        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            else if (moveStrafe < 0.0f) rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) moveForward = 1.0f;
            else if (moveForward < 0.0f) moveForward = -1.0f;
        }

        double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));

        return new double[] {posX, posZ};
    }

    public static double[] strafe2(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward != 0.0f) {
            if (strafe > 0.0f) yaw += ((forward > 0.0f) ? -45 : 45);
            else if (strafe < 0.0f) yaw += ((forward > 0.0f) ? 45 : -45);
        }

        double hypot = MathKt.hypot(mc.player.motionX, mc.player.motionZ);

        speed = EntityUtil.applySpeedEffect(mc.player, speed);
        speed = Math.min(hypot, mc.player.onGround ? speed : Math.max(hypot + 0.02, speed));

        double motionX = -TrigonometryKt.sin(yaw) * speed;
        double motionZ = TrigonometryKt.cos(yaw) * speed;

        return new double[] { motionX, motionZ };
    }

}