package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.block.Block;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;

public class Zoom extends Module {
    public Zoom() {
        super("Zoom", "Zoom", Category.MOVEMENT);
    }

    public void update() {
        setSpeed(9.9);
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 0.0000001, mc.player.posZ)).getBlock() == Block.getBlockById(9)) {
            mc.player.fallDistance = 0.0f;
            mc.player.motionX = 0.0;
            mc.player.motionY = 0.06f;
            mc.player.jumpMovementFactor = 0.01f;
            mc.player.motionZ = 0.0;


        }
    }

    public void setSpeed(double speed) {
        double forward = new MovementInput().moveForward;
        double strafe = new MovementInput().moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            mc.player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
}
