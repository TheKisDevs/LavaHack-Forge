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
        /*if(collideCheck(pos))
            return null;
        List<EnumFacing> facings = Stream.of(EnumFacing.HORIZONTALS).filter(facing -> !collideCheck(pos.offset(facing))).collect(Collectors.toList());
        if(facings.size() == 0)
            return getSingle(pos);
        if(facings.size() == 1)
            return getDouble(pos);
        if(facings.size() == 2 && facings.get(0).getOpposite() != facings.get(1))
            return getQuadruple(pos);
        return null;*/
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

        /*EnumFacing firstFacing = getFacing(pos, offsets.get(0));
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
        );*/
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
        /*List<BlockPos> offsetList = new ArrayList<>();
        for(BlockPos blockPos : list)
            offsetList.addAll(Stream.of(EnumFacing.HORIZONTALS).map(blockPos::offset).collect(Collectors.toList()));
        offsetList.removeAll(list);*/
        Safety safety = null;
        for(BlockPos blockPos : offsetList) {
            if (!isHoleBlock(blockPos))
                return null;

            safety = updateSafety(getBlockType(blockPos), safety);
        }

        /*for(BlockPos blockPos : offsetList)
            safety = updateSafety(getBlockType(blockPos), safety);*/
//        if(safety == null)
//            return null;
        return safety == null ? null : new Hole(list, type, safety);
    }

    /*public static EnumFacing getFacing(BlockPos pos, BlockPos blockPos){
        for(EnumFacing facing : EnumFacing.HORIZONTALS)
            if(pos.offset(facing) == blockPos)
                return facing;
        return null;
    }*/

    public static Hole getSingle(BlockPos pos){
        if(collideCheck(pos, true))
            return null;
        List<BlockPos> blocks = Arrays.stream(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList());
        blocks.add(pos.down());
        Safety safety = getSafety(blocks);
        if(safety == null || !isAccessible(pos)) return null;
        return new Hole(Collections.singletonList(pos), Type.Single, safety);
    }

    public static Hole getDouble(BlockPos pos){
        if(collideCheck(pos, true))
            return null;
        List<BlockPos> surround = Arrays.stream(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(position -> !collideCheck(position, true))
                .collect(Collectors.toList());
        if(surround.size() != 1)
            return null;
        BlockPos otherPos = surround.get(0);
        List<BlockPos> holeBlocks = Arrays.stream(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .collect(Collectors.toList());
        holeBlocks.addAll(
                Arrays.stream(EnumFacing.HORIZONTALS)
                        .map(otherPos::offset)
                        .collect(Collectors.toList())
        );
        holeBlocks.remove(pos);
        holeBlocks.remove(otherPos);
        Safety safety = getSafety(holeBlocks);
        if(safety == null)
            return null;
        Safety safety1 = updateSafety(getBlockType(pos.down()), safety);
        Safety safety2 = updateSafety(getBlockType(otherPos.down()), safety);
        if(safety1 == null && safety2 == null)
            return null;
        Type type = (safety1 == null || safety2 == null) ? Type.UnsafeDouble : Type.Double;
        boolean valid = newProtocol ? isAccessible(pos) && isAccessible(otherPos) : isAccessible(pos) || isAccessible(otherPos);
        if(!valid)
            return null;
        return new Hole(Arrays.asList(pos, otherPos), type, safety2 == null ? safety1 : safety2);
    }

    public static Hole getQuadruple(BlockPos pos){
        List<List<BlockPos>> configurations = getQuadrupleConfigurations(pos);
        for(List<BlockPos> list : configurations){
            Hole hole = getQuadrupleHole(list);
            if(hole != null)
                return hole;
        }
        return null;
    }

    private static Hole getQuadrupleHole(List<BlockPos> blocks){
        if(!collideCheck(blocks))
            return null;
        List<BlockPos> holeBlocks = new ArrayList<>();
        for(BlockPos pos : blocks)
            holeBlocks.addAll(Arrays.stream(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList()));
        holeBlocks.removeAll(blocks);
        Safety safety = getSafety(holeBlocks);
        if(safety == null)
            return null;
        Type type = null;
        List<BlockTypeData> blockTypes = blocks.stream()
                .map(BlockPos::down)
                .map(pos -> new BlockTypeData(getBlockType(pos), pos))
                .filter(data -> data.blockType != null)
                .collect(Collectors.toList());
        if(blockTypes.size() == 4)
            type = Type.Quadruple;
        if(blockTypes.size() == 1)
            type = Type.UnsafeQuadruple;
        if(blockTypes.size() == 2){
            BlockPos pos1 = blockTypes.get(0).pos;
            BlockPos pos2 = blockTypes.get(1).pos;
            if(pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY())
                type = Type.UnsafeQuadruple;
        }
        if(type == null)
            return null;
        for(BlockTypeData blockTypeData : blockTypes){
            if(blockTypeData.blockType == null)
                continue;
            safety = updateSafety(blockTypeData.blockType, safety);
        }
        int accessibleCount = 0;
        for(BlockPos pos : blocks)
            if(isAccessible(pos))
                accessibleCount++;
        boolean valid = newProtocol ? accessibleCount == 4 : accessibleCount != 3 && accessibleCount != 0;
        int airCount = 0;
        int blockCount = 0;
        for(BlockPos pos : blocks){
            if(collideCheck(pos.up(2), true))
                blockCount++;
            if(!collideCheck(pos.up(), true))
                airCount++;
        }
        valid |= blockCount < 4 && airCount == 4;
        return !valid ? null : new Hole(blocks, type, safety);
    }

    private static List<List<BlockPos>> getQuadrupleConfigurations(BlockPos pos){
        List<List<BlockPos>> list = new ArrayList<>();
        for(EnumFacing facing : EnumFacing.HORIZONTALS){
            EnumFacing secondFacing = facing.rotateY();
            list.add(Arrays.asList(
                    pos,
                    pos.offset(facing),
                    pos.offset(secondFacing),
                    pos.offset(facing).offset(secondFacing)
            ));
        }
        return list;
    }

    public static Safety getSafety(List<BlockPos> blocks){
        Safety safety = null;
        for(BlockPos pos : blocks) {
            BlockType type = getBlockType(pos);
            if(type == null)
                return null;
            safety = updateSafety(getBlockType(pos), safety);
        }
        return safety;
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

    public static boolean collideCheck(List<BlockPos> posses) {
        for(BlockPos pos : posses) if(collideCheck(pos, false)) return false;
        return true;
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

    private static class BlockTypeData {

        private final BlockType blockType;

        private final BlockPos pos;

        public BlockTypeData(BlockType blockType, BlockPos pos) {
            this.blockType = blockType;
            this.pos = pos;
        }

        public BlockType getBlockType() {
            return blockType;
        }

        public BlockPos getPos() {
            return pos;
        }
    }
}
