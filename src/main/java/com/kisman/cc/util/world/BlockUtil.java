package com.kisman.cc.util.world;

import com.kisman.cc.util.enums.dynamic.RotationEnum;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import java.util.*;

public class BlockUtil {
    public static final List<Block> blackList;
    public static final List<Block> shulkerList;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Vec3d[] antiDropOffsetList = new Vec3d[] { new Vec3d(0.0, -2.0, 0.0) };
    public static Vec3d[] platformOffsetList = new Vec3d[] { new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0) };
    public static Vec3d[] legOffsetList = new Vec3d[] { new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0) };
    public static Vec3d[] OffsetList = new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
    public static Vec3d[] antiStepOffsetList = new Vec3d[] { new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0) };
    public static Vec3d[] antiScaffoldOffsetList = new Vec3d[] { new Vec3d(0.0, 3.0, 0.0) };

    private static boolean unshift = false;

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean canBlockBeBroken(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, final boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }

        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) return -1;
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem)) {
                    if (entity instanceof EntityXPOrb) continue;
                    return 1;
                }
            }
        }

        for (EnumFacing side : getPossibleSides(pos)) {
            if (!canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) output[i] = vec3d.add(input[i]);
        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return convertVec3ds(entity.getPositionVector(), input);
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        ArrayList<Vec3d> placeTargets = new ArrayList<>();
        if (antiDrop) Collections.addAll(placeTargets, convertVec3ds(vec3d, antiDropOffsetList));
        if (platform) Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
        if (legs) Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
        Collections.addAll(placeTargets, convertVec3ds(vec3d, OffsetList));
        if (antiStep) Collections.addAll(placeTargets, convertVec3ds(vec3d, antiStepOffsetList));
        else {
            List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                for (Vec3d vector : vec3ds) {
                    BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                    switch (isPositionPlaceable(position, raytrace)) {
                        case -1:
                        case 1:
                        case 2: continue;
                        case 3:
                            placeTargets.add(vec3d.add(vector));
                            break;
                    }
                    if (antiScaffold) Collections.addAll(placeTargets, convertVec3ds(vec3d, antiScaffoldOffsetList));
                    return placeTargets;
                }
            }
        }
        if (antiScaffold) Collections.addAll(placeTargets, convertVec3ds(vec3d, antiScaffoldOffsetList));
        return placeTargets;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) offsets.add(new Vec3d(0.0, (y - 1), 0.0));
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        final ArrayList<Vec3d> vec3ds = new ArrayList<>();
        for (Vec3d vector : getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX(), (pos.getY() + height), pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - pos.getX());
            float f2 = (float)(vec.y - pos.getY());
            float f3 = (float)(vec.z - pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        } else mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static double getNearestBlockBelow() {
        for (double y = BlockUtil.mc.player.posY; y > 0.0; y -= 0.001) if (!(mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) && BlockUtil.mc.world.getBlockState(new BlockPos(BlockUtil.mc.player.posX, y, BlockUtil.mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox((IBlockAccess) mc.world, new BlockPos(0, 0, 0)) != null) return y;
        return -1.0;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    static {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    }

    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = getFirstFacing(pos);
        if (side == null) return isSneaking;
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) RotationUtils.lookAtVec3d(hitVec);
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    /**
     * Skidded lel
     */
    public static void placeBlock2(BlockPos pos, EnumHand hand, boolean rotate, boolean packet){
        EnumFacing side = getFirstFacing(pos);
        if (side == null) return;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }

        if (rotate){
            Vec3d eyesPos = getEyesPos();
            double diffX = hitVec.x - eyesPos.x;
            double diffY = hitVec.y - eyesPos.y;
            double diffZ = hitVec.z - eyesPos.z;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

            float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
            float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

            float r1 = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
            float r2 = mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch);

            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(r1, MathHelper.normalizeAngle((int) r2, 360), mc.player.onGround));
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    public static void placeBlock2(BlockPos pos, EnumHand hand, RotationEnum.Rotation rotator, boolean packet){
        EnumFacing side = getFirstFacing(pos);
        if (side == null) return;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }

        if (rotator != null) rotator.getTaskR().doTask(rotator.getTaskCBlock().doTask(pos), false);

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (rotator != null) rotator.getTaskR().doTask(rotator.getTaskCBlock().doTask(pos), true);
    }

    public static void placeBlock2(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, EnumFacing direction){
        EnumFacing side = getFirstFacing(pos);
        if (side == null) return;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }

        if (rotate){
            Vec3d eyesPos = getEyesPos();
            double diffX = hitVec.x - eyesPos.x;
            double diffY = hitVec.y - eyesPos.y;
            double diffZ = hitVec.z - eyesPos.z;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

            float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
            float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

            float r1 = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
            float r2 = mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch);

            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(r1, MathHelper.normalizeAngle((int) r2, 360), mc.player.onGround));
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        rightClickBlock(neighbour, hitVec, hand, direction, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static EnumFacing calcSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState offsetState = mc.world.getBlockState(pos.offset(side));
            boolean activated = offsetState.getBlock().onBlockActivated(mc.world, pos, offsetState, mc.player, EnumHand.MAIN_HAND, side, 0.0f, 0.0f, 0.0f);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                unshift = true;
            }
            if (offsetState.getBlock().canCollideCheck(offsetState, false) && !offsetState.getMaterial().isReplaceable()) return side;
        }
        return null;
    }


    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        final ArrayList<EnumFacing> facings = new ArrayList<>();
        if (mc.world == null || pos == null) return facings;
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false)) if (!blockState.getMaterial().isReplaceable()) facings.add(side);
        }
        return facings;
    }

    public static void placeBlock(BlockPos blockPos, boolean packet, boolean antiGlitch) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            if (!(getBlockResistance(blockPos.offset(enumFacing)) == BlockResistance.BLANK)) {
                for (Entity entity : mc.world.loadedEntityList) if (new AxisAlignedBB(blockPos).intersects(entity.getEntityBoundingBox())) return;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                if (packet) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos.offset(enumFacing), enumFacing.getOpposite(), EnumHand.MAIN_HAND, 0, 0, 0));
                else mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.offset(enumFacing), enumFacing.getOpposite(), new Vec3d(blockPos), EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                if (antiGlitch) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos.offset(enumFacing), enumFacing.getOpposite()));
                return;
            }
        }
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) continue;
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) return side;
        }
        return null;
    }

    public static EnumFacing getPlaceableSideExlude(BlockPos pos, ArrayList<EnumFacing> excluding) {
        for (EnumFacing side : EnumFacing.values()) {
            if (!excluding.contains(side)) {
                BlockPos neighbour = pos.offset(side);
                if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) continue;
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) return side;
            }
        }

        return null;
    }

    public static Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {
        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;

        return new Vec3d(newX, newY, newZ);
    }

    @SuppressWarnings("deprecation")
    public static BlockResistance getBlockResistance(BlockPos block) {
        if (mc.world.isAirBlock(block)) return BlockResistance.BLANK;
        else if (mc.world.getBlockState(block).getBlock().getBlockHardness(mc.world.getBlockState(block), mc.world, block) != -1 && !(mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))) return BlockResistance.BREAKABLE;
        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST)) return BlockResistance.RESISTANT;
        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(block).getBlock().equals(Blocks.BARRIER)) return BlockResistance.UNBREAKABLE;
        return null;
    }

    public static boolean isInHole() {
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        IBlockState blockState = mc.world.getBlockState(blockPos);
        return isBlockValid(blockState, blockPos);
    }

    public static boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
        return blockState.getBlock() == Blocks.AIR && mc.player.getDistanceSq(blockPos) >= 1.0 && mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR && (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos) || isElseHole(blockPos));
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        for (BlockPos pos : getTouchingBlocks(blockPos)) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        for (BlockPos pos : getTouchingBlocks(blockPos)) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        for (BlockPos pos : getTouchingBlocks(blockPos)) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN)) return false;
        }
        return true;
    }

    public static boolean isElseHole(BlockPos blockPos) {
        for (BlockPos pos : getTouchingBlocks(blockPos)) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || !touchingState.isFullBlock()) return false;
        }
        return true;
    }

    public static BlockPos[] getTouchingBlocks(BlockPos blockPos) {
        return new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        try {for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) if (!(entity instanceof EntityItem)) return false;} catch (ConcurrentModificationException ignored) { }
        return !isSolid(pos) && canBeClicked(pos);
    }

    public static boolean isSolid(BlockPos pos) {
        try {return mc.world.getBlockState(pos).getMaterial().isSolid();} catch (NullPointerException e) {return false;}
    }

    public enum BlockResistance {
        BLANK, BREAKABLE, RESISTANT, UNBREAKABLE
    }
}