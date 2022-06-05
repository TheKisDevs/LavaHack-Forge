package com.kisman.cc.util.world;

import com.kisman.cc.util.entity.InventoryUtil;
import com.kisman.cc.util.math.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BlockBreakDelta {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final BlockPos pos;

    private long start = -1;

    private ItemStack stack;

    private int slot;

    public BlockBreakDelta(BlockPos pos){
        this.pos = pos;
    }

    public BlockPos getBlockPos(){
        return pos;
    }

    public long getStart() {
        return start;
    }

    public void start(){
        if(start < 0) start = System.currentTimeMillis();
    }

    public void reset(){
        start = System.currentTimeMillis();
    }

    public ItemStack getStack(){
        return stack;
    }

    public int getSlot(){
        return slot;
    }

    public double getBlockProgress(){
        //if(stack == null) return mc.world.getBlockState(pos).getPlayerRelativeBlockHardness(mc.player, mc.world, pos) * 0.5;
        return MathUtil.clamp(1 - ((System.currentTimeMillis() - start) / (double) InventoryUtil.time(pos, stack)), 0, 1);
    }

    public IBlockState getBlockState(){
        return mc.world.getBlockState(pos);
    }

    public void updateBestStack(){
        IBlockState state = mc.world.getBlockState(pos);
        ItemStack bestStack = mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem);
        double bestSpeed = 0;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) continue;
            float speed = stack.getDestroySpeed(state);
            int eff;
            if (speed > 1) {
                speed += ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? (Math.pow(eff, 2) + 1) : 0);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestStack = stack;
                    bestSlot = i;
                }
            }
        }
        this.stack = bestStack;
        this.slot = bestSlot;
    }
}
