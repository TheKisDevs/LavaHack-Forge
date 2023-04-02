package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("ConstantConditions")
@ModuleInfo(
        name = "Anchor",
        desc = "Helps with holes",
        category = Category.MOVEMENT
)
public class Anchor extends Module {
    private final Setting mode = register(new Setting("Mode", this, Mode.Motion));
    private final Setting pitch = register(new Setting("Pitch", this, 60, 0, 90, false));
    private final Setting timer = register(new Setting("Timer", this, false));
    private final Setting timerValue = register(new Setting("Timer Value", this, 5, 0.1f, 20, false).setVisible(timer::getValBoolean));
    private final Setting disableAfterComplete = register(new Setting("Disable After Complete", this, false));
    private final Setting fastFall = register(new Setting("Fast Fall", this, false));
    private final Setting fastFallMotion = register(new Setting("Fast Fall Motion", this, 10, 1, 10, false).setVisible(fastFall::getValBoolean));
    private final Setting useLagTime = register(new Setting("Use Fast Fall Lag Time", this, false));
    private final Setting lagTime = register(new Setting("Fast Fall Lag Time", this, 500, 0, 1000, NumberType.TIME));
    private final Setting syncWithReverseStep = register(new Setting("Sync With Reverse Step", this, false));

    private boolean using = false;
    private final double[] oneblockPositions = new double[] { 0.42, 0.75 };
    private int packets;
    private boolean jumped = false;
    private boolean hasReverseStepDisabled = false;

    public Anchor() {
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    private boolean isBlockHole(BlockPos blockpos) {
        int holeblocks = 0;

        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;

        return holeblocks >= 9;
    }

    private Vec3d getCenter(double posX, double posY, double posZ) {
        return new Vec3d(Math.floor(posX) + 0.5D, Math.floor(posY), Math.floor(posZ) + 0.5D);
    }

    public void onEnable() {
        if(using && hasReverseStepDisabled) MoveModifier.instance.getReverseStep().setValBoolean(true);

        using = false;
    }

    public void update() {
        if (mc.world == null && mc.player == null) return;
        if(mc.player.isDead || mc.player.posY < 0) {
            using = false;
            return;
        }

        if (mc.player.rotationPitch >= pitch.getValInt()) {
            if (isBlockHole(WorldUtilKt.playerPosition().down(1)) || isBlockHole(WorldUtilKt.playerPosition().down(2)) || isBlockHole(WorldUtilKt.playerPosition().down(3)) || isBlockHole(WorldUtilKt.playerPosition().down(4))) {
                if(mode.getValString().equals(Mode.Motion.name())) {
                    Vec3d center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);

                    double xDiff = Math.abs(center.x - mc.player.posX);
                    double zDiff = Math.abs(center.z - mc.player.posZ);

                    if(!(xDiff <= 0.1 && zDiff <= 0.1)) {
                        double motionX = center.x - mc.player.posX;
                        double motionZ = center.z - mc.player.posZ;

                        mc.player.motionX = motionX / 2;
                        mc.player.motionZ = motionZ / 2;
                    }

                    using = true;
                } else if(mode.getValString().equals(Mode.Teleport.name())) {
                    if (!mc.player.onGround) this.jumped = mc.gameSettings.keyBindJump.isKeyDown();
        
                    if (!this.jumped && mc.player.fallDistance < 0.5 && isInHole() && mc.player.posY - getNearestBlockBelow() <= 1.125 && mc.player.posY - getNearestBlockBelow() <= 0.95 && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid(false)) {
                        if (!mc.player.onGround) ++this.packets;
                        if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
                            final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                            for (double position : oneblockPositions) mc.player.connection.sendPacket(new CPacketPlayer.Position((blockPos.getX() + 0.5f), mc.player.posY - position, (blockPos.getZ() + 0.5f), true));
                            mc.player.setPosition((blockPos.getX() + 0.5f), getNearestBlockBelow() + 0.1, (blockPos.getZ() + 0.5f));
                            this.packets = 0;
                        }
                    }

                } else if(mode.checkValString("MovementStop")) {
                    if(isBlockHole(mc.player.getPosition())) {
                        mc.player.motionX = 0;
                        mc.player.motionZ = 0;
                    } else {
                        Vec3d center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);

                        double xDiff = Math.abs(center.x - mc.player.posX);
                        double zDiff = Math.abs(center.z - mc.player.posZ);

                        if (!(xDiff <= 0.1 && zDiff <= 0.1)) {
                            double motionX = center.x - mc.player.posX;
                            double motionZ = center.z - mc.player.posZ;

                            mc.player.motionX = motionX / 2;
                            mc.player.motionZ = motionZ / 2;
                        }
                    }

                    using = true;
                }

                if(fastFall.getValBoolean() && !lagTimeCheck()) mc.player.motionY = -fastFallMotion.getValDouble();
            } else using = false;
        }

        if(isBlockHole(WorldUtilKt.playerPosition())) using = false;

        if(using && timer.getValBoolean()) EntityUtil.setTimer(timerValue.getValFloat());
        else EntityUtil.resetTimer();

        if(isBlockHole(WorldUtilKt.playerPosition())) {
            if(disableAfterComplete.getValBoolean()) super.setToggled(false);
            if(using) using = false;
        }

        if(using && syncWithReverseStep.getValBoolean()) {
            MoveModifier.instance.getReverseStep().setValBoolean(false);
            hasReverseStepDisabled = true;
        }

        if(hasReverseStepDisabled && !using) MoveModifier.instance.getReverseStep().setValBoolean(true);
    }

    private boolean lagTimeCheck() {
        return useLagTime.getValBoolean() && Managers.instance.passed(lagTime.getValInt());
    }

    public double getNearestBlockBelow() {
        for (double y = mc.player.posY; y > 0.0; y -= 0.001) if (!(mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) && mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0)) != null) return y;
        return -1.0;
    }

    private boolean isInHole() {
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        IBlockState blockState = mc.world.getBlockState(blockPos);
        return isBlockValid(blockState, blockPos);
    }

    private boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
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

    public enum Mode {MovementStop, Motion, Teleport}
}
