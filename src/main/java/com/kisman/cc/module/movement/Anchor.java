package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;

public class Anchor extends Module {
    public Anchor() {
        super("Anchor", "help with holes", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("Pull", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Pitch", this, 60, 0, 90, false));
    }

    private boolean isBlockHole(BlockPos blockpos) {
        int holeblocks = 0;
        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;

        return holeblocks >= 9;
    }

    private Vec3d center = Vec3d.ZERO;

    private Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    public void update() {
        if (mc.world == null && mc.player == null) return;
        if (mc.player.posY < 0) return;

        double pitch = Kisman.instance.settingsManager.getSettingByName(this, "Pitch").getValDouble();
        boolean pull = Kisman.instance.settingsManager.getSettingByName(this, "Pull").getValBoolean();

        if (mc.player.rotationPitch >= pitch) {
            if (isBlockHole(PlayerUtil.getPlayerPos().down(1)) || isBlockHole(PlayerUtil.getPlayerPos().down(2)) || isBlockHole(PlayerUtil.getPlayerPos().down(3)) || isBlockHole(PlayerUtil.getPlayerPos().down(4))) {
                if (!pull) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    mc.player.movementInput.moveForward = 0;
                    mc.player.movementInput.moveStrafe = 0;
                } else {
                    center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);

                    double xDiff = Math.abs(center.x - mc.player.posX);
                    double zDiff = Math.abs(center.z - mc.player.posZ);

                    if (xDiff <= 0.1 && zDiff <= 0.1) center = Vec3d.ZERO;
                    else {
                        double motionX = center.x - mc.player.posX;
                        double motionZ = center.z - mc.player.posZ;

                        mc.player.motionX = motionX / 2;
                        mc.player.motionZ = motionZ / 2;
                    }
                }
            }
        }
    }
}
