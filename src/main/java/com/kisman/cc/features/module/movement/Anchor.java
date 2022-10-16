package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;

import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.PlayerUtil;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.*;

@PingBypassModule
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
        super("Anchor", "Helps with holes", Category.MOVEMENT);
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
        if(using && hasReverseStepDisabled) {
            MoveModifier module = (MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier");
            module.getReverseStep().setValBoolean(true);
        }
        using = false;
    }

    public void update() {
        if (mc.world == null && mc.player == null) return;
        if(mc.player.isDead || mc.player.posY < 0) {
            using = false;
            return;
        }

        if (mc.player.rotationPitch >= pitch.getValInt()) {
            if (isBlockHole(PlayerUtil.getPlayerPos().down(1)) || isBlockHole(PlayerUtil.getPlayerPos().down(2)) || isBlockHole(PlayerUtil.getPlayerPos().down(3)) || isBlockHole(PlayerUtil.getPlayerPos().down(4))) {
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
        
                    if (!this.jumped && mc.player.fallDistance < 0.5 && BlockUtil.isInHole() && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 1.125 && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 0.95 && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
                        if (!mc.player.onGround) ++this.packets;
                        if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
                            final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                            for (double position : oneblockPositions) mc.player.connection.sendPacket(new CPacketPlayer.Position((blockPos.getX() + 0.5f), mc.player.posY - position, (blockPos.getZ() + 0.5f), true));
                            mc.player.setPosition((blockPos.getX() + 0.5f), BlockUtil.getNearestBlockBelow() + 0.1, (blockPos.getZ() + 0.5f));
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

        if(isBlockHole(PlayerUtil.getPlayerPos())) using = false;

        if(using && timer.getValBoolean()) EntityUtil.setTimer(timerValue.getValFloat());
        else EntityUtil.resetTimer();

        if(isBlockHole(PlayerUtil.getPlayerPos())) {
            if(disableAfterComplete.getValBoolean()) super.setToggled(false);
            if(using) using = false;
        }

        if(using && syncWithReverseStep.getValBoolean()) {
            MoveModifier module = (MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier");
            module.getReverseStep().setValBoolean(false);
            hasReverseStepDisabled = true;
        }

        if(hasReverseStepDisabled && !using) {
            MoveModifier module = (MoveModifier) Kisman.instance.moduleManager.getModule("MoveModifier");
            module.getReverseStep().setValBoolean(true);
        }
    }

    private boolean lagTimeCheck() {
        return useLagTime.getValBoolean() && Managers.instance.passed(lagTime.getValInt());
    }

    public enum Mode {MovementStop, Motion, Teleport}
}
