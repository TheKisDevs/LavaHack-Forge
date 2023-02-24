package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Targetable
@TargetsNearest
public class DamageIncreaser extends Module {

    private final SettingEnum<EventMode> eventMode = new SettingEnum<>("EventMode", this, EventMode.Tick).register();
    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 1, 6, false));
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent);
    private final Setting cancel = register(new Setting("Cancel", this, true));
    private final Setting predictFacing = register(new Setting("PredictFacing", this, false));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));

    public DamageIncreaser(){
        super("DamageIncreaser", Category.COMBAT);
        super.setDisplayInfo(() -> target == null ? "no target no fun" : target.getName());
    }

    @Target
    public EntityPlayer target = null;

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
    }

    @Override
    public void update() {
        if(eventMode.getValEnum() != EventMode.Update)
            return;
        doDamageIncreaser();
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(eventMode.getValEnum() != EventMode.Tick)
            return;
        doDamageIncreaser();
    }

    private void doDamageIncreaser(){
        if(mc.player == null || mc.world == null)
            return;

        target = EnemyManagerKt.nearest();

        if(target == null)
            return;

        BlockPos pos = getOptimalBlockPos();

        if(pos == null)
            return;

        placeBlock(pos);
    }

    private void placeBlock(BlockPos pos){
        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) || checkEntities(pos))
            return;
        swap.getValEnum().getTask().doTask(slot, false);
        BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private BlockPos getOptimalBlockPos(){
        BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
        if(getBlock(targetPos.down()).isReplaceable(mc.world, targetPos.down()))
            return null;
        List<BlockPos> possibleBlockPositions = Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> targetPos.down().offset(facing)).collect(Collectors.toList());
        if(predictFacing.getValBoolean())
            possibleBlockPositions.remove(targetPos.down().offset(predictFacing()));
        List<BlockPos> tempList = possibleBlockPositions.stream()
                .filter(pos -> getDistance(mc.player, pos, 0) <= placeRange.getValDouble())
                .filter(pos -> !BlockUtil.getPossibleSides(pos).isEmpty())
                .filter(pos -> getBlock(pos.up()) == Blocks.AIR)
                .filter(pos -> getBlock(pos.up(2)) == Blocks.AIR)
                .filter(pos -> !target.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.up())))
                .collect(Collectors.toList());
        BlockPos temp = tempList.stream().min(Comparator.comparingDouble(pos -> getDistance(target, pos, 1))).orElse(null);
        if(temp == null)
            return null;
        if(cancel.getValBoolean() && !getBlock(temp).isReplaceable(mc.world, temp))
            return null;
        return tempList.stream()
                .filter(pos -> getBlock(pos).isReplaceable(mc.world, pos))
                .min(Comparator.comparingDouble(pos -> getDistance(target, pos, 1)))
                .orElse(null);
    }

    private double getDistance(Entity entity, BlockPos pos, int yOffset){
        double x = pos.getX() + 0.5;
        double y = pos.getY() + yOffset;
        double z = pos.getZ() + 0.5;
        return entity.getDistance(x, y, z);
    }

    private EnumFacing predictFacing(){
        double mX = Math.abs(target.motionX);
        double mZ = Math.abs(target.motionZ);
        if(mX > mZ){
            if(target.motionX >= 0)
                return EnumFacing.EAST;
            else
                return EnumFacing.WEST;
        } else {
            if(target.motionZ >= 0)
                return EnumFacing.SOUTH;
            else
                return EnumFacing.NORTH;
        }
    }

    private Block getBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock();
    }

    private enum EventMode {
        Tick,
        Update
    }
}
