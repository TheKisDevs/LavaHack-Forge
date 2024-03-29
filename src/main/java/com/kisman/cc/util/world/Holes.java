package com.kisman.cc.util.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
    public static boolean newProtocol = false;
//    public static boolean liquidHoles = false;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<Hole> getHoles(double range) {
        return getHoles(mc.player, range);
    }

    public static List<Hole> getHoles(Entity entity, double range){
        List<BlockPos> blocks = WorldUtilKt.sphere(entity, (int) range);
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
            return new Hole(Collections.singletonList(pos), Type.Single, safety);
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
            boolean collideCheck1 = collideCheck(pos.down(), false);
            boolean collideCheck2 = collideCheck(otherPos.down(), false);
            if((collideCheck1 && !collideCheck2) || (!collideCheck1 && collideCheck2)) type = Type.UnsafeDouble;
            if(newProtocol) {
                boolean collideCheck3 = collideCheck(pos.up(), false);
                boolean collideCheck4 = collideCheck(otherPos.up(), false);

                if(collideCheck3 || collideCheck4) type = Type.UnsafeDouble;
            }
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
            return new Hole(Arrays.asList(pos, otherPos), type, safety);
        }

        BlockPos pos1 = offsets.get(0);
        BlockPos pos2 = offsets.get(1);
        BlockPos delta1 = new BlockPos(pos.getX() - pos1.getX(), pos.getY() - pos1.getY(), pos.getZ() - pos1.getZ());
        BlockPos delta2 = new BlockPos(pos.getX() - pos2.getX(), pos.getY() - pos2.getY(), pos.getZ() - pos2.getZ());

        List<BlockPos> list = Arrays.asList(
                pos,
                pos.add(delta1),
                pos.add(delta2),
                pos.add(delta1).add(delta2)
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
        if(
                !collideCheck(list.get(0).down(), false)
                || !collideCheck(list.get(1).down(), false)
                || !collideCheck(list.get(2).down(), false)
                || !collideCheck(list.get(3).down(), false)) type = Type.UnsafeQuadruple;
        if(newProtocol && (
                collideCheck(list.get(0).up(), false)
                || collideCheck(list.get(1).up(), false)
                || collideCheck(list.get(2).up(), false)
                || collideCheck(list.get(3).up(), false))) type = Type.UnsafeQuadruple;
        Set<BlockPos> offsetList = WorldUtilKt.highlight(list);
        Safety safety = null;
        for(BlockPos blockPos : offsetList) {
            if (!isHoleBlock(blockPos))
                return null;

            safety = updateSafety(getBlockType(blockPos), safety);
        }

        return safety == null ? null : new Hole(list, type, safety);
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
        return !collideCheck(pos.up(1), true) && !collideCheck(pos.up(2), true);
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

        public Hole(List<BlockPos> holeBlocks, Type type, Safety safety) {
            this.holeBlocks = holeBlocks;
            this.aabb = makeAABB(holeBlocks);
            this.type = type;
            this.safety = safety;
        }

        private AxisAlignedBB makeAABB(List<BlockPos> blocks){
            double minX = 0;
            double maxX = 0;
            double minZ = 0;
            double maxZ = 0;
            boolean initialize = true;
            for(BlockPos pos : blocks){
                if(initialize){
                    minX = pos.getX();
                    maxX = pos.getX() + 1;
                    minZ = pos.getZ();
                    maxZ = pos.getZ() + 1;
                    initialize = false;
                    continue;
                }
                minX = Math.min(pos.getX(), minX);
                maxX = Math.max(pos.getX() + 1, maxX);
                minZ = Math.min(pos.getZ(), minZ);
                maxZ = Math.max(pos.getZ() + 1, maxZ);
            }
            return new AxisAlignedBB(minX, blocks.get(0).getY(), minZ, maxX, blocks.get(0).getY() + 1, maxZ);
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