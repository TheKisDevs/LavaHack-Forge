package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.util.RotationUtils;
import com.mojang.realmsclient.dto.PlayerInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoCrystal extends Module{
    public static boolean stopAC = true;

    public AutoCrystal() {
        super("AutoCrystal", "ezzz", Category.COMBAT);
    }

    public void update() {
        if (mc.player == null && mc.world == null) {
            return;
        }

        if (stopAC) {
            return;
        }

        breakCrystal();
    }

    private void breakCrystal() {
        mc.world.loadedEntityList.stream().filter(entity -> mc.player.getDistance(entity) < 4.25f).filter(entity -> entity instanceof EntityEnderCrystal).forEach(entity -> {
            mc.player.cameraPitch = RotationUtils.getRotation(mc.player)[1];
            mc.player.cameraYaw = RotationUtils.getRotation(entity)[0];
            mc.player.rotationYawHead = mc.player.cameraYaw;
            mc.player.renderYawOffset = mc.player.cameraYaw;
            mc.player.rotationPitch = mc.player.cameraPitch;
            mc.getConnection().sendPacket(new CPacketUseEntity(entity));
            mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        });
    }
}
