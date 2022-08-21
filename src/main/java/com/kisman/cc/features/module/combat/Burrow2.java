package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.BlockEnum;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

//TODO: crystal breaker
public class Burrow2 extends Module {
    private final Setting offset = register(new Setting("Offset", this, 7, -20, 20, false));
    private final Setting smartOffset = register(new Setting("SmartOffset", this, false));
    private final Setting block = register(new Setting("Block", this, BlockEnum.Blocks.Obsidian));
    private final Setting swap = register(new Setting("Switch", this, SwapEnum2.Swap.Silent));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting centerPlayer = register(new Setting("Center", this, false));
    private final Setting floorY = register(new Setting("FloorY", this, false).setVisible(centerPlayer::getValBoolean));
    private final Setting smart = register(new Setting("Smart", this, false));
    private final Setting smartRange = register(new Setting("SmartRange", this, 3.0, 1.0, 8.0, false).setVisible(smart::getValBoolean));
    private final Setting smartOnGround = register(new Setting("SmartOnGround", this, false));
    private final Setting keepOn = register(new Setting("KeepOn", this, false));

    public Burrow2(){
        super("Burrow", Category.COMBAT);
    }

    private BlockPos oldPos = null;

    private int swapSlot(){
        return InventoryUtil.getBlockInHotbar(((BlockEnum.Blocks) block.getValEnum()).getTask().doTask());
    }

    private void swap(int slot, boolean swapBack){
        ((SwapEnum2.Swap) swap.getValEnum()).getTask().doTask(slot, swapBack);
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

    private void placeBlock(BlockPos pos, int slot){
        int oldSlot = mc.player.inventory.currentItem;
        swap(slot, false);
        BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        swap(oldSlot, true);
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
    public void onEnable(){
        if(mc.player == null || mc.world == null){
            this.setToggled(false);
            return;
        }
        oldPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if(centerPlayer.getValBoolean())
            centerPlayer();
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

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

        if(smart.getValBoolean() && mc.world.playerEntities.stream().noneMatch(player -> mc.player.getDistanceSq(player) <= smartRange.getValDouble()))
            return;

        fakeJump();

        placeBlock(oldPos, slot);

        mc.player.setPosition(mc.player.posX, mc.player.posY - 1.16610926093821D, mc.player.posZ);

        double off = getOffset();
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, false));

        if(keepOn.getValBoolean())
            return;

        toggle();
    }

    @Override
    public void onDisable(){
        oldPos = null;
    }
}
