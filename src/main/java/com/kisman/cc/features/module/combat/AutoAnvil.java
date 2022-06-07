package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class AutoAnvil extends Module {

    private final Setting center = register(new Setting("Setting", this, true));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting toggleOnComplete = register(new Setting("ToggleOnComplete", this, true));

    public AutoAnvil(){
        super("AutoAnvil", Category.COMBAT);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null) return;

        int oldSlot = mc.player.inventory.currentItem;

        int oSlot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);

        int aSlot = InventoryUtil.getBlockInHotbar(Blocks.ANVIL);

        if(oldSlot == -1 || aSlot == -1) return;

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        List<BlockPos> blocks = getBlocks();

        for(BlockPos pos : blocks){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oSlot));
            mc.player.inventory.currentItem = oSlot;

            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());

            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.inventory.currentItem = oldSlot;
        }

        mc.player.connection.sendPacket(new CPacketHeldItemChange(aSlot));
        mc.player.inventory.currentItem = aSlot;

        BlockUtil.placeBlock2(playerPos.up(2), EnumHand.MAIN_HAND, false, false);

        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        mc.player.inventory.currentItem = oldSlot;

        if(toggleOnComplete.getValBoolean())
            toggle();
    }

    private void swap(int slot, boolean swapBack){
    }

    private void  centerPlayer(){
        double x = floor(mc.player.posX) + 0.5;
        double z = floor(mc.player.posZ) + 0.5;
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, mc.player.posY, z, mc.player.onGround));
        mc.player.setPosition(x, mc.player.posY, z);
    }

    private double floor(double a){
        double x = 0;
        if(a < 0.0)
            x = 1.0;
        return (long) a - x;
    }

    private List<BlockPos> getBlocks(){
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<BlockPos> surround = getSurroundBlocks(playerPos.up(2));
        List<BlockPos> blocks = new ArrayList<>(16);
        for(BlockPos pos : surround){
            if(mc.world.getBlockState(pos).getMaterial().isSolid())
                return blocks;
        }
        return getBlocksOffset(playerPos.up(2), mc.player.getHorizontalFacing().getOpposite());
    }

    private List<BlockPos> getBlocksOffset(BlockPos pos, EnumFacing facing){
        List<BlockPos> blocks = new ArrayList<>(16);
        BlockPos off = pos.offset(facing);
        off = off.down(3);
        for(int i = 0; i < 4; i++){
            if(mc.world.getBlockState(off).getMaterial().isReplaceable())
                blocks.add(off);
            off = off.up();
        }
        return blocks;
    }

    private List<BlockPos> getSurroundBlocks(BlockPos pos){
        List<BlockPos> list = new ArrayList<>(16);
        list.add(pos.north());
        list.add(pos.east());
        list.add(pos.south());
        list.add(pos.west());
        return list;
    }

    private boolean hasSpace(){
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        boolean res = true;
        for(int i = 0; i < 3; i++){
            res &= mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
            pos = pos.up();
        }
        return res;
    }
}
