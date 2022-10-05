package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.WorkInProgress;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cubic
 * @since 05.10.2022
 */
@WorkInProgress
public class CevBreaker extends Module {

    private final SettingGroup trapGroup = register(new SettingGroup(new Setting("Trap", this)));
    private final Setting trapMode = register(trapGroup.add(new Setting("TrapMode", this, TrapMode.Full)));
    private final Setting trapSwap = register(trapGroup.add(new Setting("TrapSwitch", this, SwapEnum2.Swap.Silent).setTitle("Switch")));
    private final Setting trapHelpingBlocks = register(trapGroup.add(new Setting("HelpingBlocks", this, false)));
    private final Setting trapFeetBlocks = register(trapGroup.add(new Setting("FeetBlocks", this, false)));
    private final Setting trapAntiStep = register(trapGroup.add(new Setting("AntiStep", this, false)));
    private final Setting trapDynamic = register(trapGroup.add(new Setting("Dynamic", this, false)));
    private final Setting rotate = register(trapGroup.add(new Setting("Rotate", this, false)));
    private final Setting packet = register(trapGroup.add(new Setting("Packet", this, false)));

    public CevBreaker(){
        super("CevBreaker", "Are ya satisfied, banckie?", Category.COMBAT);
    }

    private final Supplier<BlockListProvider> blockProvider = () -> trapDynamic.getValBoolean() ? new DynamicProvider() : new StaticProvider();

    private int getSwitchSlot(){
        return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
    }

    private void placeTrapBlocks(Entity entity){
        List<BlockPos> blockPos = getTrapBlocks(entity);
        int slot = getSwitchSlot();
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        ((SwapEnum2.Swap) trapSwap.getValEnum()).getTask().doTask(slot, false);
        blockPos.forEach(pos -> BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean()));
        ((SwapEnum2.Swap) trapSwap.getValEnum()).getTask().doTask(oldSlot, true);
    }

    private List<BlockPos> getTrapBlocks(Entity entity){
        List<BlockPos> blocks = blockProvider.get().getTrapBlocks((TrapMode) trapMode.getValEnum(), entity);
        if(trapHelpingBlocks.getValBoolean())
            blocks.addAll(0, blockProvider.get().getHelpingBlocks(entity));
        if(trapFeetBlocks.getValBoolean())
            blocks.addAll(0, blockProvider.get().getFeetBlocks(entity));
        if(trapAntiStep.getValBoolean())
            blocks.addAll(blockProvider.get().getAntiStepBlocks(entity));
        return blocks;
    }

    private static BlockPos getBlockPos(Entity entity){
        return new BlockPos(entity.posX, entity.posY, entity.posZ);
    }

    private static abstract class BlockListProvider {
        public abstract List<BlockPos> getTrapBlocks(TrapMode trapMode, Entity entity);
        public abstract List<BlockPos> getHelpingBlocks(Entity entity);
        public abstract List<BlockPos> getFeetBlocks(Entity entity);
        public abstract List<BlockPos> getAntiStepBlocks(Entity entity);
    }

    private static class StaticProvider extends BlockListProvider {
        @Override public List<BlockPos> getTrapBlocks(TrapMode trapMode, Entity entity) {
            List<BlockPos> list = new ArrayList<>();
            BlockPos pos = getBlockPos(entity);
            Stream<EnumFacing> stream = Arrays.stream(EnumFacing.HORIZONTALS);
            stream.forEach(facing -> list.add(pos.offset(facing)));
            if(trapMode == TrapMode.Full)
                stream.forEach(facing -> list.add(pos.up().offset(facing)));
            else
                list.add(pos.up().north());
            list.add(pos.up(2).north());
            list.add(pos.up(2));
            return list;
        }

        @Override public List<BlockPos> getHelpingBlocks(Entity entity) {
            return Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getBlockPos(entity).down().offset(facing)).collect(Collectors.toList());
        }

        @Override public List<BlockPos> getFeetBlocks(Entity entity) {
            return Collections.singletonList(getBlockPos(entity).down());
        }

        @Override public List<BlockPos> getAntiStepBlocks(Entity entity) {
            return Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getBlockPos(entity).up(2).offset(facing)).collect(Collectors.toList());
        }
    }

    private static class DynamicProvider extends BlockListProvider {
        @Override public List<BlockPos> getTrapBlocks(TrapMode trapMode, Entity entity) {
            List<BlockPos> list = new ArrayList<>(getDynamicBlocks(entity, 0));
            if(trapMode == TrapMode.Full)
                list.addAll(getDynamicBlocks(entity, 1));
            else
                list.add(list.get(0).up());
            list.add(list.get(0).up(2));
            list.addAll(getDynamicBlocksOffset(entity, 2));
            return list;
        }

        @Override public List<BlockPos> getHelpingBlocks(Entity entity) {
            return getDynamicBlocks(entity, -1);
        }

        @Override public List<BlockPos> getFeetBlocks(Entity entity) {
            return getDynamicBlocksOffset(entity, -1);
        }

        @Override public List<BlockPos> getAntiStepBlocks(Entity entity) {
            return getDynamicBlocksOffset(entity, 2);
        }

        private static List<BlockPos> getDynamicBlocks(Entity entity, int offset){
            List<BlockPos> list = getDynamicBlocksOffset(entity, offset);
            List<BlockPos> result = new ArrayList<>();
            list.forEach(pos -> Arrays.stream(EnumFacing.HORIZONTALS).forEach(facing -> result.add(pos.offset(facing))));
            return result.stream().filter(pos -> !list.contains(pos)).collect(Collectors.toList());
        }

        private static List<BlockPos> getDynamicBlocksOffset(Entity entity, int offset){
            List<BlockPos> list = new ArrayList<>(16);
            AxisAlignedBB aabb = entity.getEntityBoundingBox();
            double oX = (aabb.maxX - aabb.minX) / 2.0;
            double oZ = (aabb.maxZ - aabb.minZ) / 2.0;
            list.add(new BlockPos(new Vec3d(entity.posX + oX, entity.posY + offset, entity.posZ + oZ)));
            list.add(new BlockPos(new Vec3d(entity.posX + oX, entity.posY + offset, entity.posZ - oZ)));
            list.add(new BlockPos(new Vec3d(entity.posX - oX, entity.posY + offset, entity.posZ + oZ)));
            list.add(new BlockPos(new Vec3d(entity.posX - oX, entity.posY + offset, entity.posZ - oZ)));
            return list;
        }
    }

    private enum TrapMode {
        Full,
        Head
    }
}
