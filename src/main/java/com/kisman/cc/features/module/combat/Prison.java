package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.BlockUtil2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Prison extends Module {

    private final Setting targetRange = register(new Setting("TargetRange", this, 8, 1, 15, false));
    private final Setting placeRange = register(new Setting("PlaceRange", this, 5.5, 1, 6, false));
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent).register();
    //private final Setting exponent = register(new Setting("Exponent", this, 1, 2, 5, true));
    //private final Setting digit = register(new Setting("Digit", this, 5, 0, 9, true));
    private final Setting support = register(new Setting("Support", this, true));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));

    public Prison(){
        super("Prison", Category.COMBAT);
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        EntityPlayer target = EntityUtil.getTarget(targetRange.getValFloat());
        if(target == null)
            return;

        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;

        /*
        double distance = digit.getValDouble() / Math.pow(10, exponent.getValDouble());

        Info info = getInfo(target);
        if(info == null)
            return;

        if(info.distance > distance)
            return;
         */

        BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);

        double x = target.posX + target.motionX;
        double z = target.posZ + target.motionZ;

        BlockPos blockPos = new BlockPos(x, target.posY, z);

        if(blockPos.equals(targetPos))
            return;

        EnumFacing enumFacing = Stream.of(EnumFacing.HORIZONTALS).filter(facing -> targetPos.offset(facing).equals(blockPos)).findFirst().orElse(null);

        if(enumFacing == null)
            return;

        List<BlockPos> list = getBlocks(target, enumFacing);

        if(mc.world.getBlockState(list.get(0)).getBlock().isReplaceable(mc.world, list.get(0)) && mc.world.getBlockState(list.get(1)).getBlock().isReplaceable(mc.world, list.get(1)))
            return;

        BlockPos supportPos = targetPos.offset(enumFacing).down();
        if(support.getValBoolean() && BlockUtil.getPossibleSides(supportPos).isEmpty())
            list.add(0, supportPos);

        for(BlockPos pos : list){
            if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
                continue;
            if(checkEntities(pos))
                continue;
            if(Math.sqrt(mc.player.getDistanceSq(pos)) > placeRange.getValDouble())
                continue;
            swap.getValEnum().getTask().doTask(slot, false);
            BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), true, packet.getValBoolean());
            swap.getValEnum().getTask().doTask(oldSlot, true);
        }
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private List<BlockPos> getBlocks(EntityPlayer player, EnumFacing facing){
        List<BlockPos> list = new ArrayList<>();
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ).offset(facing);
        list.add(pos);
        list.add(pos.up());
        list.add(pos.up(2));
        return list;
    }

    private Info getInfo(EntityPlayer player){
        double fX = Math.floor(player.posX);
        double fZ = Math.floor(player.posZ);
        double dX = player.posX - fX;
        double dZ = player.posZ - fZ;
        double oX = 1.0 - dX;
        double oZ = 1.0 - dZ;
        double a = Math.min(dX, oX);
        double b = Math.min(dZ, oZ);
        double c = Math.min(a, b);
        if(c == dX)
            return new Info(EnumFacing.WEST, dX);
        if(c == dZ)
            return new Info(EnumFacing.NORTH, dZ);
        if(c == oX)
            return new Info(EnumFacing.EAST, oX);
        if(c == oZ)
            return new Info(EnumFacing.SOUTH, oZ);
        return null;
    }

    private static class Info {

        public final EnumFacing facing;

        public final double distance;

        public Info(EnumFacing facing, double distance) {
            this.facing = facing;
            this.distance = distance;
        }
    }
}
