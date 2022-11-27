package com.kisman.cc.util.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cubic
 * @since 25.11.2022
 * Work in progress
 */
public class Holes {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<Hole> getHoles(double range){
        List<BlockPos> blocks = CrystalUtils.getSphere((float) range, true, false);
        List<Hole> holes = new ArrayList<>();
        Set<BlockPos> alreadyChecked = new HashSet<>();
        for(BlockPos pos : blocks){
            if(alreadyChecked.contains(pos))
                continue;
            Hole hole = getHole(pos);
            if(hole == null)
                continue;
            alreadyChecked.addAll(hole.getHoleBlocks());
            holes.add(hole);
        }
        return holes;
    }

    public static Hole getHole(BlockPos pos){
        if(collideCheck(pos, true))
            return null;
        if(!collideCheck(pos.down(), false))
            return null;
        List<BlockPos> offsets = Stream.of(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(Holes::isHoleBlock)
                .collect(Collectors.toList());
        if(offsets.size() <= 1)
            return null;
        if(offsets.size() >= 4){
            if(!isAccessible(pos))
                return null;
            Safety safety = null;
            for(BlockPos blockPos : offsets)
                safety = updateSafety(getBlockType(blockPos), safety);
            return new Hole(Arrays.asList(pos), new AxisAlignedBB(pos), Type.Single, safety);
        }
        if(offsets.size() >= 3){
            BlockPos otherPos = Stream.of(EnumFacing.HORIZONTALS)
                    .map(pos::offset)
                    .filter(blockPos -> !offsets.contains(blockPos))
                    .findFirst()
                    .orElse(null);
            if(otherPos == null)
                return null;
            if(!isAccessible(pos) && !isAccessible(otherPos))
                return null;
            Type type = Type.Double;
            if(!collideCheck(otherPos.down(), false))
                type = Type.UnsafeDouble;
            Safety safety = null;
            List<BlockPos> list = Stream.of(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList());
            list.addAll(Stream.of(EnumFacing.HORIZONTALS).map(otherPos::offset).collect(Collectors.toList()));
            list.remove(pos);
            list.remove(otherPos);
            for(BlockPos blockPos : list)
                if(!isHoleBlock(blockPos))
                    return null;
            for(BlockPos blockPos : list)
                safety = updateSafety(getBlockType(blockPos), safety);
            if(safety == null)
                return null;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                    Math.min(pos.getX(), otherPos.getX()),
                    Math.min(pos.getY(), otherPos.getY()),
                    Math.min(pos.getZ(), otherPos.getZ()),
                    Math.max(pos.getX(), otherPos.getX()) + 1,
                    Math.max(pos.getY(), otherPos.getY()) + 1,
                    Math.max(pos.getZ(), otherPos.getZ()) + 1
            );
            return new Hole(Arrays.asList(pos, otherPos), axisAlignedBB, type, safety);
        }
        EnumFacing firstFacing = getFacing(pos, offsets.get(0));
        EnumFacing secondFacing = getFacing(pos, offsets.get(1));
        if(firstFacing == null || secondFacing == null)
            return null;
        firstFacing = firstFacing.getOpposite();
        secondFacing = secondFacing.getOpposite();
        List<BlockPos> list = Arrays.asList(
                pos,
                pos.offset(firstFacing),
                pos.offset(secondFacing),
                pos.offset(firstFacing).offset(secondFacing)
        );
        boolean accessible = false;
        for(BlockPos blockPos : list) {
            if (!isAccessible(blockPos))
                continue;
            accessible = true;
            break;
        }
        if(!accessible)
            return null;
        for(BlockPos blockPos : list)
            if(collideCheck(blockPos, true))
                return null;
        Type type = Type.Quadruple;
        for(BlockPos blockPos : list){
            if(collideCheck(blockPos.down(), false))
                continue;
            type = Type.UnsafeQuadruple;
            break;
        }
        List<BlockPos> offsetList = new ArrayList<>();
        for(BlockPos blockPos : list)
            offsetList.addAll(Stream.of(EnumFacing.HORIZONTALS).map(blockPos::offset).collect(Collectors.toList()));
        offsetList.removeAll(list);
        for(BlockPos blockPos : offsetList)
            if(!isHoleBlock(blockPos))
                return null;
        Safety safety = null;
        for(BlockPos blockPos : offsetList)
            safety = updateSafety(getBlockType(blockPos), safety);
        if(safety == null)
            return null;
        BlockPos min = pos;
        for(BlockPos blockPos : list)
            if(blockPos.getX() < min.getX() && blockPos.getZ() < min.getZ())
                min = blockPos;
        BlockPos max = pos;
        for(BlockPos blockPos : list)
            if(blockPos.getX() > max.getX() && blockPos.getZ() > max.getZ())
                max = blockPos;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(min, max);
        return new Hole(list, axisAlignedBB, type, safety);
    }

    public static EnumFacing getFacing(BlockPos pos, BlockPos blockPos){
        for(EnumFacing facing : EnumFacing.HORIZONTALS)
            if(pos.offset(facing) == blockPos)
                return facing;
        return null;
    }

    public static Safety updateSafety(BlockType blockType, Safety previousSafety){
        if(previousSafety == null)
            return blockType.getSafety();
        if(previousSafety == Safety.Bedrock){
            if(blockType == BlockType.Bedrock)
                return Safety.Bedrock;
            return Safety.Mix;
        }
        if(previousSafety == Safety.Obsidian){
            if(blockType == BlockType.Bedrock)
                return Safety.Mix;
            return Safety.Obsidian;
        }
        return Safety.Mix;
    }

    public static boolean isHoleBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public static BlockType getBlockType(BlockPos pos){
        if(mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
            return BlockType.Obsidian;
        if(mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)
            return BlockType.Bedrock;
        return null;
    }

    public static boolean isAccessible(BlockPos pos){
        boolean result1 = false;
        for(int i = 0; i < 2; i++)
            result1 |= collideCheck(pos.up(i + 1), true);
        boolean result2 = false;
        for(EnumFacing facing : EnumFacing.HORIZONTALS) {
            boolean r = true;
            for (int i = 0; i < 2; i++) {
                if (collideCheck(pos.up(i + 1).offset(facing), true)){
                    r = false;
                    break;
                }
            }
            result2 |= r;
        }
        return (!result1) | result2;
    }

    public static boolean collideCheck(BlockPos pos, boolean liquid){
        IBlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlock().canCollideCheck(blockState, liquid);
    }

    public static class Hole {

        private final List<BlockPos> holeBlocks;

        private final AxisAlignedBB aabb;

        private final Type type;

        private final Safety safety;

        public Hole(List<BlockPos> holeBlocks, AxisAlignedBB aabb, Type type, Safety safety) {
            this.holeBlocks = holeBlocks;
            this.aabb = aabb;
            this.type = type;
            this.safety = safety;
        }

        public List<BlockPos> getHoleBlocks() {
            return holeBlocks;
        }

        public AxisAlignedBB getAabb() {
            return aabb;
        }

        public Type getType() {
            return type;
        }

        public Safety getSafety() {
            return safety;
        }
    }

    public enum Type {
        Single,
        Double,
        UnsafeDouble,
        Quadruple,
        UnsafeQuadruple
    }

    public enum Safety {
        Bedrock,
        Obsidian,
        Mix
    }

    public enum BlockType {
        Bedrock(Safety.Bedrock),
        Obsidian(Safety.Obsidian);

        private final Safety safety;

        BlockType(Safety safety) {
            this.safety = safety;
        }

        public Safety getSafety() {
            return safety;
        }
    }
}
