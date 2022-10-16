package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScaffoldTest2 extends Module {

    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent).register();
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));

    public ScaffoldTest2(){
        super("ScaffoldTest2", Category.DEBUG);
    }

    private int playerY;

    private List<BlockPos> lastPlaced = new ArrayList<>();

    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        this.playerY = (int) Math.floor(mc.player.posY);
        this.lastPlaced.clear();
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        int newY = (int) Math.floor(mc.player.posY);
        BlockPos playerPos = new BlockPos(mc.player.posX, playerY, mc.player.posZ);
        List<BlockPos> list = getBlocks(mc.player.posX, playerY - 1, mc.player.posZ);
        if(!BlockUtil.getPossibleSides(playerPos).isEmpty())
            list = Arrays.asList(playerPos);
        if(/*alreadyPlaced(mc.player.posX, playerY - 1, mc.player.posZ) ||*/ (lastPlaced.size() >= 2 && list.stream().filter(pos -> lastPlaced.contains(pos)).count() > 1)){
            this.playerY = newY;
            return;
        }
        //if(mc.player.motionX == 0 && mc.player.motionZ == 0)
        //    list = Arrays.asList(playerPos);
        //if(newY > playerY)
        //    list.add(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));
        swap.getValEnum().getTask().doTask(slot, false);
        for(BlockPos pos : list){
            if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) || checkEntities(pos))
                continue;
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        }
        swap.getValEnum().getTask().doTask(oldSlot, true);
        this.playerY = newY;
        this.lastPlaced = list;
    }

    private boolean alreadyPlaced(double x, double y, double z){
        List<BlockPos> standOnBlocks = Stream.of(
                new BlockPos(x + 0.3, y, z + 0.3),
                new BlockPos(x + 0.3, y, z - 0.3),
                new BlockPos(x - 0.3, y, z + 0.3),
                new BlockPos(x - 0.3, y, z - 0.3)
        ).distinct().collect(Collectors.toList());
        int placeCount = 0;
        for(BlockPos pos : standOnBlocks)
            if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
                placeCount++;
        return placeCount >= 3;
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    @SuppressWarnings("ALL")
    private List<BlockPos> getBlocks(double x, double y, double z){
        List<Bind<Double, BlockPos>> calculatedAreas = calculateAreas(x, y, z);
        if(calculatedAreas.size() == 0)
            return Arrays.asList();
        if(calculatedAreas.size() == 1)
            return Arrays.asList(calculatedAreas.iterator().next().getSecond());
        List<BlockPos> list = new ArrayList<>();
        for(Bind<Double, BlockPos> pos : calculatedAreas)
            list.add(pos.getSecond());
        if(list.size() == 2)
            return list;
        List<BlockPos> newList = new ArrayList<>();
        newList.add(calculatedAreas.stream().max((b1, b2) -> Double.compare(b1.getFirst(), b2.getFirst())).get().getSecond());
        newList.add(calculatedAreas.stream().min((b1, b2) -> Double.compare(b1.getFirst(), b2.getFirst())).get().getSecond());
        calculatedAreas.removeAll(newList);
        newList.add(calculatedAreas.stream().max((b1, b2) -> Double.compare(b1.getFirst(), b2.getFirst())).get().getSecond());
        return newList;
    }

    // y should be already adjusted
    private List<Bind<Double, BlockPos>> calculateAreas(double x, double y, double z){
        List<BlockPos> standOnBlocks = Stream.of(
                new BlockPos(x + 0.3, y, z + 0.3),
                new BlockPos(x + 0.3, y, z - 0.3),
                new BlockPos(x - 0.3, y, z + 0.3),
                new BlockPos(x - 0.3, y, z - 0.3)
        ).distinct().collect(Collectors.toList());
        if(standOnBlocks.size() < 1) // WTF?
            return Collections.emptyList();
        if(standOnBlocks.size() == 1)
            return Collections.singletonList(new Bind<>(0.36, standOnBlocks.get(0)));
        if(standOnBlocks.size() == 2){
            BlockPos first = standOnBlocks.get(0);
            BlockPos second = standOnBlocks.get(1);
            if(first.getZ() == second.getZ()){
                double iX = Math.round(x);
                double a = 0.6 * Math.abs((x + 0.3) - iX);
                List<Bind<Double, BlockPos>> list = new ArrayList<>();
                list.add(new Bind<>(a, first.getX() > second.getX() ? first : second));
                list.add(new Bind<>(0.36 - a, first.getX() <= second.getX() ? first : second));
                return list;
            } else {
                double iZ = Math.round(z);
                double a = 0.6 * Math.abs((z + 0.3) - iZ);
                List<Bind<Double, BlockPos>> list = new ArrayList<>();
                list.add(new Bind<>(a, first.getZ() > second.getZ() ? first : second));
                list.add(new Bind<>(0.36 - a, first.getZ() <= second.getZ() ? first : second));
                return list;
            }
        }
        double iX = Math.round(x);
        double iZ = Math.round(z);
        double nD = Math.abs((z - 0.3) - iZ);
        double eD = Math.abs((x + 0.3) - iX);
        double sD = Math.abs((z + 0.3) - iZ);
        double wD = Math.abs((x - 0.3) - iX);
        List<Bind<Double, BlockPos>> list = new ArrayList<>();
        list.add(new Bind<>(sD * eD, standOnBlocks.get(0)));
        list.add(new Bind<>(nD * eD, standOnBlocks.get(1)));
        list.add(new Bind<>(wD * sD, standOnBlocks.get(2)));
        list.add(new Bind<>(wD * nD, standOnBlocks.get(3)));
        return list;
    }
}
