package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

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
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoCrystal extends Module{
    public static boolean stopAC = false;

    public AutoCrystal() {
        super("AutoCrystal", "ezzz", Category.COMBAT);
    }

    public void update() {
        if (mc.player == null || mc.world == null || mc.player.isDead) {
            return;
        }

        if (stopAC) {
            return;
        }

        /*PlayerInfo player = new PlayerInfo(mc.player, false);*/
    }
}
