package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", "", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("Mode", this, "Matrix", new ArrayList<>(Arrays.asList("Matrix", "Solid"))));

        Kisman.instance.settingsManager.rSetting(new Setting("SpeedMatrix", this, 0.6f, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("SpeedSolid", this, 1, 0, 2, false));
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        String mode = Kisman.instance.settingsManager.getSettingByName(this, "Mode").getValString();

        if(mode.equalsIgnoreCase("Matrix")) {
            float speed = (float) Kisman.instance.settingsManager.getSettingByName(this, "SpeedMatrix").getValDouble();

            if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - -0.37f, mc.player.posZ)).getBlock() == Blocks.WATER) {
                mc.player.jump();
                mc.player.jumpMovementFactor = 0;

                mc.player.motionX *= speed;
                mc.player.motionZ *= speed;
                mc.player.onGround = false;

                if(mc.player.isInWater() || mc.player.isInLava()) {
                    mc.player.onGround = false;
                }
            }
        } else if(mode.equalsIgnoreCase("Solid")) {
            float speed = (float) Kisman.instance.settingsManager.getSettingByName(this, "SpeedSolid").getValDouble();

            if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() == Block.getBlockById(9)) {
                mc.player.motionY = 0.18f;
            } else if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 0.0000001, mc.player.posZ)).getBlock() == Block.getBlockById(9)) {
                mc.player.fallDistance = 0.0f;
                mc.player.motionX = 0.0;
                mc.player.motionY = 0.06f;

                mc.player.jumpMovementFactor = speed;

                mc.player.motionY = 0;
            }
        }
    }
}
