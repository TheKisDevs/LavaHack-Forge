package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.network.play.client.CPacketEntityAction.Action.*;

public class Burrow extends Module {
    private Setting rotate = new Setting("Rotate", this, false);
    private Setting offset = new Setting("Offset", this, -7, 20, 20, false);
    private Setting sneak = new Setting("Sneak", this, false);

    private BlockPos oldPos;
    private int oldSlot = -1;

    public Burrow() {
        super("Burrow", "random skid moment", Category.COMBAT);

        setmgr.rSetting(rotate);
        setmgr.rSetting(offset);
        setmgr.rSetting(sneak);
    }

    public void onEnable() {
        if(mc.player == null && mc.world == null) {
            super.setToggled(false);
            return;
        }

        oldPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if(mc.world.getBlockState(oldPos).getBlock() == Blocks.OBSIDIAN || EntityUtil.intersectsWithEntity(oldPos)) {
            super.setToggled(false);
            return;
        }

        oldSlot = mc.player.inventory.currentItem;
    }

    public void update() {
        if (InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) != -1) {
            ChatUtils.error("Can't find obsidian in hotbar!");
            super.setToggled(false);
            return;
        }

        InventoryUtil.switchToSlot(InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9), true);

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));

        boolean sneaking = mc.player.isSneaking();
        if (sneak.getValBoolean()) {
            if (sneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_SNEAKING));
            }
        }

        BlockUtil.placeBlockSmartRotate(oldPos, EnumHand.MAIN_HAND, rotate.getValBoolean(), true, false);

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValDouble(), mc.player.posZ, false));

        InventoryUtil.switchToSlot(oldSlot, true);

        if (sneak.getValBoolean()) {
            if (sneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, STOP_SNEAKING));
            }
        }

        super.setToggled(false);
    }
}
