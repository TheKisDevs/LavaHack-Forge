package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class BreakAlert extends Module {
    public BreakAlert() {
        super("BreakAlert", Category.COMBAT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(getSurroundBlocks().isEmpty()) return;
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for(BlockPos pos : getSurroundBlocks()) if(BlockUtil.canBlockBeBroken(pos)) blocks.add(pos);
        ArrayList<BlockPos> blocksForAlert = new ArrayList<>();
//        for(BlockPos pos : blocks) pos.
    }

    private ArrayList<BlockPos> getSurroundBlocks() {
        int z;
        int x;
        double decimalX = Math.abs(mc.player.posX) - Math.floor(Math.abs(mc.player.posX));
        double decimalZ = Math.abs(mc.player.posZ) - Math.floor(Math.abs(mc.player.posZ));
        int lengthX = calculateLength(decimalX, false);
        int negativeLengthX = calculateLength(decimalX, true);
        int lengthZ = calculateLength(decimalZ, false);
        int negativeLengthZ = calculateLength(decimalZ, true);
        ArrayList<BlockPos> tempOffsets = new ArrayList<>();
        for (x = 1; x < lengthX + 1; ++x) {
            tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
            tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
        }
        for (x = 0; x <= negativeLengthX; ++x) {
            tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
            tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
        }
        for (z = 1; z < lengthZ + 1; ++z) {
            tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
            tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
        }
        for (z = 0; z <= negativeLengthZ; ++z) {
            tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
            tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
        }
        return tempOffsets;
    }

    private int calculateLength(double decimal, boolean negative) {
        if (negative) return decimal <= Double.longBitsToDouble(Double.doubleToLongBits(30.561776836994962) ^ 0x7FEDBCE3A865B81CL) ? 1 : 0;
        return decimal >= Double.longBitsToDouble(Double.doubleToLongBits(22.350511399288944) ^ 0x7FD03FDD7B12B45DL) ? 1 : 0;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(mc.player.posX, mc.player.posY - Math.floor(mc.player.posY) > Double.longBitsToDouble(Double.doubleToLongBits(19.39343307331816) ^ 0x7FDAFD219E3E896DL) ? Math.floor(mc.player.posY) + Double.longBitsToDouble(Double.doubleToLongBits(4.907271931218261) ^ 0x7FE3A10BE4A4A510L) : Math.floor(mc.player.posY), mc.player.posZ);
    }

    private BlockPos addToPosition(BlockPos pos, double x, double z) {
        block1: {
            if (pos.getX() < 0) x = -x;
            if (pos.getZ() >= 0) break block1;
            z = -z;
        }
        return pos.add(x, Double.longBitsToDouble(Double.doubleToLongBits(1.4868164896774578E308) ^ 0x7FEA7759ABE7F7C1L), z);
    }
}
