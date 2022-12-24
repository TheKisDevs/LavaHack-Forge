package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.movement.CornerClip;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.BurrowModes;
import com.kisman.cc.util.enums.BurrowStages;
import com.kisman.cc.util.enums.DiagonalDirections;
import com.kisman.cc.util.enums.dynamic.BlockEnum;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

//TODO: crystal breaker
public class Burrow2 extends Module {
    private final Setting mode = register(new Setting("Mode", this, BurrowModes.CrystalPvPcc));
    private final Setting reconfigCornerClip = register(new Setting("Reconfig Corner Clip", this, false).setVisible(() -> mode.getValEnum() != BurrowModes.Normal));
    private final Setting clipsCount = register(new Setting("Clips Count", this, 1, 1, 5, true).setVisible(() -> mode.getValEnum() != BurrowModes.Normal));
    private final SettingEnum<DiagonalDirections> direction = new SettingEnum<>("Direction", this, DiagonalDirections.XpZp).setVisible(() -> mode.getValEnum() != BurrowModes.Normal).register();
    private final Setting placeHelpingBlocks = register(new Setting("Place Helping Blocks", this, false).setVisible(() -> mode.getValEnum() != BurrowModes.Normal));
    private final Setting placeDelay = register(new Setting("Place Delay", this, 100, 0, 1000, NumberType.TIME));
    private final Setting offset = register(new Setting("Offset", this, 7, -20, 20, false));
    private final Setting smartOffset = register(new Setting("SmartOffset", this, false));
    private final Setting block = register(new Setting("Block", this, BlockEnum.Blocks.Obsidian));
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent).register();
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting centerPlayer = register(new Setting("Center", this, false).setVisible(() -> mode.getValEnum() == BurrowModes.Normal));
    private final Setting floorY = register(new Setting("FloorY", this, false).setVisible(centerPlayer::getValBoolean));
    private final Setting noPushOut = register(new Setting("NoPushOutBlock", this, false));
    private final Setting smart = register(new Setting("Smart", this, false));
    private final Setting smartRange = register(new Setting("SmartRange", this, 3.0, 1.0, 8.0, false).setVisible(smart::getValBoolean));
    private final Setting smartOnGround = register(new Setting("SmartOnGround", this, false));
    public final Setting keepOn = register(new Setting("KeepOn", this, false));
    private final Setting dynamic = register(new Setting("Dynamic", this, false));
    private final Setting down = register(new Setting("Down", this, false));
    private final Setting setBack = register(new Setting("SetBack", this, false));
    private final Setting placeUpperBlock = register(new Setting("Place Upper Block", this, false));

    public static Burrow2 instance;

    public Burrow2(){
        super("Burrow", Category.COMBAT);
        instance = this;
    }

    private BlockPos oldPos = null;

    private BurrowStages stage = BurrowStages.Centering;
    private int clipped = 0;

    private final ArrayList<BlockPos> toPlace = new ArrayList<>();
    private final ArrayList<BlockPos> placed = new ArrayList<>();

    private final TimerUtils placeTimer = new TimerUtils();

    private void reconfigCornerClip() {
        CornerClip.instance.timeout.setValDouble(1);
        CornerClip.instance.disableSetting.setValBoolean(true);

        reconfigCornerClip.setValBoolean(false);
    }

    private int swapSlot(){
        return InventoryUtil.getBlockInHotbar(((BlockEnum.Blocks) block.getValEnum()).getTask().doTask());
    }

    private void swap(int slot, boolean swapBack){
        swap.getValEnum().getTask().doTask(slot, swapBack);
    }

    private boolean checkSafe(BlockPos pos){
        if(!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return false;
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity.equals(mc.player) || entity instanceof EntityItem || entity instanceof EntityXPOrb)
                continue;
            return false;
        }
        return true;
    }

    private void centerPlayer(){
        double x = Math.floor(mc.player.posX) + 0.5;
        double y = floorY.getValBoolean() ? Math.floor(mc.player.posY) : mc.player.posY;
        double z = Math.floor(mc.player.posZ) + 0.5;
        boolean onGround = ((long) y != y) || mc.world.getBlockState(new BlockPos(x, y, z).down()).getMaterial().isReplaceable();
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, !smartOnGround.getValBoolean() || onGround));
    }

    private void fakeJump(){
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ);
    }

    private void placeBlock(BlockPos pos, int slot) {
        if(mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            int oldSlot = mc.player.inventory.currentItem;
            swap(slot, false);
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
            swap(oldSlot, true);
        }
    }

    private double getOffset(){
        if(!smartOffset.getValBoolean() || offset.getValDouble() < 2.0)
            return offset.getValDouble();
        for(int i = 0; i <= Math.ceil(offset.getValDouble()); i++){
            BlockPos pos1 = new BlockPos(mc.player.posX + 0.3, mc.player.posY + i + 2.0, mc.player.posZ + 0.3);
            BlockPos pos2 = new BlockPos(mc.player.posX + 0.3, mc.player.posY + i + 2.0, mc.player.posZ - 0.3);
            BlockPos pos3 = new BlockPos(mc.player.posX - 0.3, mc.player.posY + i + 2.0, mc.player.posZ + 0.3);
            BlockPos pos4 = new BlockPos(mc.player.posX - 0.3, mc.player.posY + i + 2.0, mc.player.posZ - 0.3);
            boolean b1 = !mc.world.getBlockState(pos1).getMaterial().isReplaceable();
            boolean b2 = !mc.world.getBlockState(pos2).getMaterial().isReplaceable();
            boolean b3 = !mc.world.getBlockState(pos3).getMaterial().isReplaceable();
            boolean b4 = !mc.world.getBlockState(pos4).getMaterial().isReplaceable();
            if(b1 || b2 || b3 || b4)
                return i - 1.0;
        }
        return offset.getValDouble();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            this.setToggled(false);
            return;
        }
        oldPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if(centerPlayer.getValBoolean() && mode.getValEnum() == BurrowModes.Normal)
            centerPlayer();

        if(mode.getValEnum() != BurrowModes.Normal) reconfigCornerClip();

        stage = BurrowStages.Centering;
        clipped = 0;
    }

    private void doNormalMode() {
        int slot = swapSlot();

        if(slot == -1){
            if(!keepOn.getValBoolean())
                toggle();
            return;
        }

        if(!checkSafe(oldPos)){
            if(!keepOn.getValBoolean())
                toggle();
            return;
        }

        if(smart.getValBoolean() && mc.world.playerEntities.stream().noneMatch(player -> mc.player.getDistance(player) <= smartRange.getValDouble()))
            return;

        fakeJump();

        if(dynamic.getValBoolean()) {
            int oldSlot = mc.player.inventory.currentItem;
            swap(slot, false);
            for(BlockPos pos : SurroundRewrite.instance.getDynamicBlocksOffset(mc.player, oldPos.getY(), 0)) {
                BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());

                if (placeUpperBlock.getValBoolean()) BlockUtil.placeBlock2(pos.up(), EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
            }
            swap(oldSlot, true);
        } else {
            placeBlock(oldPos, slot);

            if(placeUpperBlock.getValBoolean()) placeBlock(oldPos.up(), slot);
        }

        if(down.getValBoolean() && setBack.getValBoolean())
            mc.player.setPosition(mc.player.posX, Math.floor(mc.player.posX - 1), mc.player.posZ);

        if(!mc.isSingleplayer()) {
            mc.player.setPosition(mc.player.posX, mc.player.posY - 1.16610926093821D, mc.player.posZ);

            double off = getOffset();
            if(down.getValBoolean())
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - (setBack.getValBoolean() ? off : off + 1), mc.player.posZ, false));
            else
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, false));
        }

        if(keepOn.getValBoolean())
            return;

        toggle();
    }

    private void doCrystalPvPCCMode() {
        if(stage == BurrowStages.Centering) {
            double x = Math.floor(mc.player.posX);
            double y = floorY.getValBoolean() ? Math.floor(mc.player.posY) : mc.player.posY;
            double z = Math.floor(mc.player.posZ);

            if (direction.getValEnum() == DiagonalDirections.XpZp) {
                x += 1 - WorldUtilKt.playerBoxBorderLength();
                z += 1 - WorldUtilKt.playerBoxBorderLength();
            } else if (direction.getValEnum() == DiagonalDirections.XmZm) {
                x += WorldUtilKt.playerBoxBorderLength();
                z += WorldUtilKt.playerBoxBorderLength();
            } else if (direction.getValEnum() == DiagonalDirections.XpZm) {
                x += 1 - WorldUtilKt.playerBoxBorderLength();
                z += WorldUtilKt.playerBoxBorderLength();
            } else if (direction.getValEnum() == DiagonalDirections.XmZp) {
                x += WorldUtilKt.playerBoxBorderLength();
                z += 1 - WorldUtilKt.playerBoxBorderLength();
            }

            boolean onGround = ((long) y != y) || mc.world.getBlockState(new BlockPos(x, y, z).down()).getMaterial().isReplaceable();
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, !smartOnGround.getValBoolean() || onGround));

            stage = placeHelpingBlocks.getValBoolean() ? BurrowStages.PreparePlacing : BurrowStages.Clipping;
        } else if(stage == BurrowStages.PreparePlacing) {
            BlockPos pos1 = oldPos.offset(direction.getValEnum().getDirection1());
            BlockPos pos2 = oldPos.offset(direction.getValEnum().getDirection2());

            BlockPos upperPos1 = pos1.up();
            BlockPos upperPos2 = pos2.up();

            toPlace.clear();
            toPlace.add(pos1);
            toPlace.add(pos2);
            toPlace.add(upperPos1);
            toPlace.add(upperPos2);

            placed.clear();

            placeTimer.reset();

            stage = BurrowStages.Placing;
        } else if(stage == BurrowStages.Placing) {
            int slot = swapSlot();

            if(!placeTimer.passedMillis(placeDelay.getValInt())) return;

            placeTimer.reset();

            for(BlockPos pos : toPlace) {
                if(placed.contains(pos)) continue;

                placeBlock(pos, slot);

                placed.add(pos);

                return;
            }

            stage = BurrowStages.Clipping;
        } else if(stage == BurrowStages.Clipping) {
            if(!CornerClip.instance.isToggled()) {
                CornerClip.instance.enable();

                clipped++;

                if (clipped == clipsCount.getValInt()) stage = BurrowStages.Burrowing;
            }
        } else if(stage == BurrowStages.Burrowing) {
            doNormalMode();

            stage = BurrowStages.Centering;
        }
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mode.getValEnum() == BurrowModes.Normal) doNormalMode();
        else doCrystalPvPCCMode();
    }

    @SubscribeEvent
    public void onPlayerPushOutEvent(PlayerSPPushOutOfBlocksEvent event){
        if(noPushOut.getValBoolean())
            event.setCanceled(true);
    }
}
