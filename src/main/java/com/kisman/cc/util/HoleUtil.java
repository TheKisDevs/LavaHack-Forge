package com.kisman.cc.util;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HoleUtil implements Globals {

    public static boolean isPartOfHole(BlockPos blockPos) {
        List<Entity> entities = new ArrayList<>();
        entities.addAll(mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, new AxisAlignedBB(blockPos.add(1, 0, 0))));
        entities.addAll(mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, new AxisAlignedBB(blockPos.add(-1, 0, 0))));
        entities.addAll(mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, new AxisAlignedBB(blockPos.add(0, 0, 1))));
        entities.addAll(mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, new AxisAlignedBB(blockPos.add(0, 0, -1))));
        return entities.stream().anyMatch(entity -> entity instanceof EntityPlayer);
    }

    public static BlockSafety isBlockSafe(Block block) {
        if (block == Blocks.BEDROCK) {
            return BlockSafety.UNBREAKABLE;
        }
        if (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) {
            return BlockSafety.RESISTANT;
        }
        return BlockSafety.BREAKABLE;
    }

    public static boolean isInHole(double posX, double posY, double posZ) {
        return isObsidianHole(new BlockPos(posX, Math.round(posY), posZ)) || isBedRockHole(new BlockPos(posX, Math.round(posY), posZ));
    }

    public static boolean isInHole(Entity entity) {
        return isObsidianHole(new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ)) || isBedRockHole(new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ));
    }

    public static HoleInfo isHole(BlockPos centreBlock, boolean onlyOneWide, boolean ignoreDown) {
        HoleInfo output = new HoleInfo();
        HashMap<BlockOffset, BlockSafety> unsafeSides = HoleUtil.getUnsafeSides(centreBlock);

        if (unsafeSides.containsKey(HoleUtil.BlockOffset.DOWN)) {
            if (unsafeSides.remove(HoleUtil.BlockOffset.DOWN, HoleUtil.BlockSafety.BREAKABLE)) {
                if (!ignoreDown) {
                    output.setSafety(BlockSafety.BREAKABLE);
                    return output;
                }
            }
        }

        int size = unsafeSides.size();

        unsafeSides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);

        // size has changed so must have weak side
        if (unsafeSides.size() != size) {
            output.setSafety(BlockSafety.RESISTANT);
        }

        size = unsafeSides.size();

        // is it a perfect hole
        if (size == 0) {
            output.setType(HoleType.SINGLE);
            output.setCentre(new AxisAlignedBB(centreBlock));
            return output;
        }
        // have one open side
        else if (size == 1 && !onlyOneWide) {
            return isDoubleHole(output, centreBlock, unsafeSides.keySet().stream().findFirst().get());
        } else {
            output.setSafety(BlockSafety.BREAKABLE);
            return output;
        }
    }

    private static HoleInfo isDoubleHole(HoleInfo info, BlockPos centreBlock, BlockOffset weakSide) {
        BlockPos unsafePos = weakSide.offset(centreBlock);

        HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> unsafeSides = HoleUtil.getUnsafeSides(unsafePos);

        int size = unsafeSides.size();

        unsafeSides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);

        // size has changed so must have weak side
        if (unsafeSides.size() != size) {
            info.setSafety(BlockSafety.RESISTANT);
        }

        if (unsafeSides.containsKey(HoleUtil.BlockOffset.DOWN)) {
            info.setType(HoleType.CUSTOM);
            unsafeSides.remove(HoleUtil.BlockOffset.DOWN);
        }

        // is it a safe hole
        if (unsafeSides.size() > 1) {
            info.setType(HoleType.NONE);
            return info;
        }

        // it is
        double minX = Math.min(centreBlock.getX(), unsafePos.getX());
        double maxX = Math.max(centreBlock.getX(), unsafePos.getX()) + 1;
        double minZ = Math.min(centreBlock.getZ(), unsafePos.getZ());
        double maxZ = Math.max(centreBlock.getZ(), unsafePos.getZ()) + 1;

        info.setCentre(new AxisAlignedBB(minX, centreBlock.getY(), minZ, maxX, centreBlock.getY() + 1, maxZ));

        if (info.getType() != HoleType.CUSTOM) {
            info.setType(HoleType.DOUBLE);
        }
        return info;
    }

    public static HashMap<BlockOffset, BlockSafety> getUnsafeSides(BlockPos pos) {
        HashMap<BlockOffset, BlockSafety> output = new HashMap<>();
        BlockSafety temp;

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.DOWN.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.DOWN, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.NORTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.NORTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.SOUTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.SOUTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.EAST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.EAST, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.WEST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.WEST, temp);

        return output;
    }

    public enum BlockSafety {
        UNBREAKABLE,
        RESISTANT,
        BREAKABLE
    }

    public enum HoleType {
        SINGLE,
        DOUBLE,
        CUSTOM,
        NONE
    }

    public static class HoleInfo {
        private HoleType type;
        private BlockSafety safety;

        private AxisAlignedBB centre;

        public HoleInfo() {
            this(BlockSafety.UNBREAKABLE, HoleType.NONE);
        }

        public HoleInfo(BlockSafety safety, HoleType type) {
            this.type = type;
            this.safety = safety;
        }

        public void setType(HoleType type) {
            this.type = type;
        }

        public void setSafety(BlockSafety safety) {
            this.safety = safety;
        }

        public void setCentre(AxisAlignedBB centre) {
            this.centre = centre;
        }

        public HoleType getType() {
            return type;
        }

        public BlockSafety getSafety() {
            return safety;
        }

        public AxisAlignedBB getCentre() {
            return centre;
        }
    }

    public enum BlockOffset {
        DOWN(0, -1, 0),
        UP(0, 1, 0),
        NORTH(0, 0, -1),
        EAST(1, 0, 0),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0);

        private final int x;
        private final int y;
        private final int z;

        BlockOffset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos offset(BlockPos pos) {
            return pos.add(x, y, z);
        }

        public BlockPos forward(BlockPos pos, int scale) {
            return pos.add(x * scale, 0, z * scale);
        }

        public BlockPos backward(BlockPos pos, int scale) {
            return pos.add(-x * scale, 0, -z * scale);
        }

        public BlockPos left(BlockPos pos, int scale) {
            return pos.add(z * scale, 0, -x * scale);
        }

        public BlockPos right(BlockPos pos, int scale) {
            return pos.add(-z * scale, 0, x * scale);
        }
    }

    public static boolean isObsidianHole(BlockPos blockPos) {
        return !(BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) != BlockUtil.BlockResistance.BLANK || isBedRockHole(blockPos) || BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockUtil.BlockResistance.UNBREAKABLE);
    }

    public static boolean isBedRockHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockUtil.BlockResistance.UNBREAKABLE;
    }
}