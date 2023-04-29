package com.kisman.cc.util.movement;

import com.kisman.cc.util.math.TrigonometryKt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

import java.util.Objects;

public class MovementUtil {
    public static final double WALK_SPEED = 0.221;
    public static final double DEFAULT_SPEED = 0.2873;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static double getBaseMoveSpeed() {
        return getMoveSpeed(0.2873);
    }

    public static double getMoveSpeed(double speed) {
        return getMoveSpeed(speed, 1);
    }

    public static double getMoveSpeed(double speed, double multiplier) {
        if (mc.player != null && mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier();
            speed *= (1.0 + 0.2 * (amplifier + 1)) * multiplier;
        }
        return speed;
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

        double posX = moveForward * speed * -TrigonometryKt.sin(TrigonometryKt.toRadians(rotationYaw)) + moveStrafe * speed * TrigonometryKt.cos(TrigonometryKt.toRadians(rotationYaw));
        double posZ = moveForward * speed * TrigonometryKt.cos(TrigonometryKt.toRadians(rotationYaw)) - moveStrafe * speed * -TrigonometryKt.sin(TrigonometryKt.toRadians(rotationYaw));

        return new double[] {posX, posZ};
    }
}