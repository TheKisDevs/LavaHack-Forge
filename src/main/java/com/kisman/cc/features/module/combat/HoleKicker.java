package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingEnum;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.collections.Pair;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoleKicker extends Module {

    private final SettingEnum<RedstoneMode> redstoneMode = new SettingEnum<>("RedstoneMode", this, RedstoneMode.Torch);
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent);
    private final Setting range = new Setting("Range", this, 5, 1, 10, false);
    private final Setting rotate = new Setting("Rotate", this, false);
    private final Setting packetPlace = new Setting("PacketPlace", this, false);

    public HoleKicker(){
        super("HoleKicker", Category.COMBAT);
        setmgr.rSetting(redstoneMode);
        setmgr.rSetting(swap);
        setmgr.rSetting(range);
        setmgr.rSetting(range);
        setmgr.rSetting(packetPlace);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        EntityPlayer target = EntityUtil.getTarget(range.getValFloat());

        if(target == null)
            return;

        RedstoneMode rm = redstoneMode.getValEnum();

        int piston = InventoryUtil.getBlockInHotbar(Blocks.PISTON);
        if(piston == -1)
            piston = InventoryUtil.getBlockInHotbar(Blocks.STICKY_PISTON);
        if(piston == -1)
            return;

        int redstone = InventoryUtil.getBlockInHotbar(getBlock());
        if(redstone == -1){
            rm = getOppositeMode(rm);
            redstone = InventoryUtil.getBlockInHotbar(getOppositeBlock());
        }
        if(redstone == -1)
            return;

        int old = mc.player.inventory.currentItem;

        BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);

        EnumFacing facing = getEnumFacing(pos);

        if(facing == null)
            return;

        Pair<BlockPos> pair = getPlacements(pos, facing, rm);

        if(pair == null)
            return;

        swap.getValEnum().getTask().doTask(piston, false);

        BlockUtil.placeBlock2(pair.getFirst(), EnumHand.MAIN_HAND, rotate.getValBoolean(), packetPlace.getValBoolean());

        swap.getValEnum().getTask().doTask(redstone, false);

        BlockUtil.placeBlock2(pair.getSecond(), EnumHand.MAIN_HAND, rotate.getValBoolean(), packetPlace.getValBoolean());

        swap.getValEnum().getTask().doTask(old, true);

        mc.world.getBlockState(pair.getFirst()).getBlock().rotateBlock(mc.world, pair.getFirst(), facing.getOpposite());

        //this.toggle();
    }

    private RedstoneMode getOppositeMode(RedstoneMode rm){
        if(rm == RedstoneMode.Torch)
            return RedstoneMode.Block;
        return RedstoneMode.Torch;
    }

    private Block getBlock(){
        if(redstoneMode.getValEnum() == RedstoneMode.Torch)
            return Blocks.REDSTONE_TORCH;
        return Blocks.REDSTONE_BLOCK;
    }

    private Block getOppositeBlock(){
        if(redstoneMode.getValEnum() == RedstoneMode.Torch)
            return Blocks.REDSTONE_BLOCK;
        return Blocks.REDSTONE_TORCH;
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private EnumFacing getEnumFacing(BlockPos pos){
        List<EnumFacing> facings = new ArrayList<>();
        for(EnumFacing enumFacing : EnumFacing.HORIZONTALS){
            BlockPos offset = pos.offset(enumFacing).up();
            if(!mc.world.getBlockState(offset).getBlock().isReplaceable(mc.world, offset))
                continue;
            if(checkEntities(offset))
                continue;
            facings.add(enumFacing);
        }
        return facings.stream().min((o1, o2) -> {
            BlockPos offset1 = pos.offset(o1).up();
            BlockPos offset2 = pos.offset(o2).up();
            return Double.compare(mc.player.getDistance(offset1.getX(), offset1.getY(), offset1.getZ()), mc.player.getDistance(offset2.getX(), offset2.getY(), offset2.getZ()));
        }).orElse(null);
    }

    private Pair<BlockPos> getPlacements(BlockPos pos, EnumFacing facing, RedstoneMode rm){
        BlockPos offset = pos.offset(facing);
        if(mc.world.getBlockState(offset).getBlock().isReplaceable(mc.world, offset))
            return null;
        offset = offset.up();
        if(rm == RedstoneMode.Block)
            return new Pair<>(offset, offset.offset(facing));
        BlockPos torchPos = null;
        for(EnumFacing enumFacing : Arrays.stream(EnumFacing.HORIZONTALS).filter(ef -> ef != facing.getOpposite()).collect(Collectors.toList())){
            BlockPos off = offset.offset(enumFacing);
            if(BlockUtil.getPossibleSides(off).stream().anyMatch(side -> side != EnumFacing.UP))
                torchPos = off;
        }
        if(torchPos == null)
            return null;
        return new Pair<>(offset, torchPos);
    }

    private enum RedstoneMode {
        Torch,
        Block
    }
}
