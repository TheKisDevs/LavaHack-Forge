package com.kisman.cc.util.world;

import com.kisman.cc.Kisman;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public class HoleUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private boolean newProtocol;

    public HoleUtils(){
        this.newProtocol = false;
    }

    public HoleUtils newProtocol(boolean newProtocol){
        this.newProtocol = newProtocol;
        return this;
    }

    public HoleUtils inLiquid(boolean inLiquid){
        return this;
    }


    public List<Hole> getHoles(double range){
        List<BlockPos> blocks = WorldUtilKt.sphere((int) range).stream().filter(pos -> !collideCheck(pos)).collect(Collectors.toList());
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

    public Hole getHole(BlockPos pos){
        Hole single = getSingle(pos);
        Hole doubleHole = getDouble(pos);
        Hole quadruple = getQuadruple(pos);
        if(single != null)
            return single;
        if(doubleHole != null)
            return doubleHole;
        if(quadruple != null)
            return quadruple;
        return null;
    }

    public Hole getSingle(BlockPos pos){
        if(collideCheck(pos))
            return null;
        List<BlockPos> blocks = Arrays.stream(EnumFacing.HORIZONTALS).map(pos::offset).collect(Collectors.toList());
        blocks.add(pos.down());
        Safety safety = getSafety(blocks);
        if(safety == null)
            return null;
        if(!isAccessible(pos))
            return null;
        return new Hole(Arrays.asList(pos), Type.Single, safety);
    }

    public Hole getDouble(BlockPos pos){
        if(collideCheck(pos))
            return null;
        List<BlockPos> surround = Arrays.stream(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(position -> !collideCheck(position))
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

    public Hole getQuadruple(BlockPos pos){
        List<List<BlockPos>> configurations = getQuadrupleConfigurations(pos);
        for(List<BlockPos> list : configurations){
            Hole hole = getQuadrupleHole(list);
            if(hole != null)
                return hole;
        }
        return null;
    }

    private Hole getQuadrupleHole(List<BlockPos> blocks){
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
        int nullCount = 0;
        List<BlockType> blockTypes = blocks.stream()
                .map(BlockPos::down)
                .map(this::getBlockType)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(blockTypes.size() == 4)
            type = Type.Quadruple;
        if(blockTypes.size() == 1)
            type = Type.UnsafeQuadruple;
        if(type == null)
            return null;
        for(BlockType blockType : blockTypes){
            if(blockType == null)
                continue;
            safety = updateSafety(blockType, safety);
        }
        int accessibleCount = 0;
        for(BlockPos pos : blocks)
            if(isAccessible(pos))
                accessibleCount++;
        boolean valid = newProtocol ? accessibleCount == 4 : accessibleCount != 3;
        if(!valid)
            return null;
        return new Hole(blocks, type, safety);
    }

    private List<List<BlockPos>> getQuadrupleConfigurations(BlockPos pos){
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

    public boolean isAccessible(BlockPos pos){
        List<BlockPos> checks = Arrays.asList(pos.up(), pos.up(2));
        boolean b1 = collideCheck(checks);
        boolean b2 = false;
        for(EnumFacing facing : EnumFacing.HORIZONTALS)
            b2 |= collideCheck(checks.stream().map(positon -> positon.offset(facing)).collect(Collectors.toList()));
        return b1 && (!collideCheck(pos.up(3)) || b2);
    }

    public Safety getSafety(List<BlockPos> blocks){
        Safety safety = null;
        for(BlockPos pos : blocks) {
            BlockType type = getBlockType(pos);
            if(type == null)
                return null;
            safety = updateSafety(getBlockType(pos), safety);
        }
        return safety;
    }

    public Safety updateSafety(BlockType blockType, Safety previousSafety){
        if(blockType == null)
            return null;
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

    public boolean isHoleBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public BlockType getBlockType(BlockPos pos){
        if(mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
            return BlockType.Obsidian;
        if(mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)
            return BlockType.Bedrock;
        return null;
    }

    private Block getBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock();
    }

    private boolean collideCheck(List<BlockPos> blocks){
        for(BlockPos pos : blocks)
            if(collideCheck(pos))
                return false;
        return true;
    }

    private boolean collideCheck(BlockPos pos){
        IBlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlock().canCollideCheck(blockState, false);
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
