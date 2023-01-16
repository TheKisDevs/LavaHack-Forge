package com.kisman.cc.util.entity.player;

import com.kisman.cc.util.enums.SoftBlocks;
import com.kisman.cc.util.world.block.ExtendedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int findValidScaffoldBlockHotbarSlot() {
        for(int i = 0; i <= 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            Item currentItem = stack.getItem();
            if(!(currentItem instanceof ItemBlock)) continue;
            Block currentBlock = Block.getBlockFromItem(stack.getItem());
            if (!Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock()) continue;
            if (currentBlock instanceof BlockFalling) continue;

            return i;
        }

        return -1;
    }

    public static int findSoftBlocks(int min, int max) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            Item currentItem = stack.getItem();
            if(!(currentItem instanceof ItemBlock)) continue;
            Block currentBlock = Block.getBlockFromItem(currentItem);

            for(SoftBlocks softBlock : SoftBlocks.values()) {
                if(!softBlock.getItems().isEmpty()) for (Item item : softBlock.getItems()) if(currentItem.equals(item)) return i;
                if(!softBlock.getBlocks().isEmpty()) for (Block block : softBlock.getBlocks()) if(currentBlock.equals(block)) return i;
            }
        }

        return -1;
    }

    public static int findItemInInventory(Item item) {
        if(mc.player != null) {
            for (int i = mc.player.inventoryContainer.getInventory().size() - 1; i > 0; --i) {
                if (i == 5 || i == 6 || i == 7 || i == 8) continue;

                ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

                if (s.isEmpty()) continue;
                if (s.getItem() == item) return i;
            }
        } return -1;
    }

    public long time(BlockPos pos) {
        return time(pos, EnumHand.MAIN_HAND);
    }

    public long time(BlockPos pos, EnumHand hand) {
        return time(pos, mc.player.getHeldItem(hand));
    }

    public static long time(BlockPos pos, ItemStack stack) {
        final IBlockState state = mc.world.getBlockState(pos);
        final Block block = state.getBlock();
        float toolMultiplier = stack.getDestroySpeed(state);

        toolMultiplier += Math.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack), 2) + 1;

        if (mc.player.isPotionActive(MobEffects.HASTE)) toolMultiplier *= 1.0F + ( float ) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
                    break;
            }
            toolMultiplier *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            toolMultiplier /= 5.0F;
        }

        float dmg = toolMultiplier / state.getBlockHardness(mc.world, pos);

        if (canHarvestBlock(block, pos, stack) || block == Blocks.ENDER_CHEST) dmg /= 30;
        else dmg /= 100;
        float ticks = ( float ) (Math.floor(1.0f / dmg) + 1.0f);

        return ( long ) ((ticks / 20.0f) * 1000);
    }

    public static boolean canHarvestBlock(Block block, BlockPos pos, ItemStack stack) {
        IBlockState state = mc.world.getBlockState(pos);
        state = state.getBlock().getActualState(state, mc.world, pos);
        if (state.getMaterial().isToolNotRequired()) return true;
        String tool = block.getHarvestTool(state);
        if (stack.isEmpty() || tool == null) return mc.player.canHarvestBlock(state);
        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);
        if (toolLevel < 0) return mc.player.canHarvestBlock(state);
        return toolLevel >= block.getHarvestLevel(state);
    }

    public static int findBestToolSlot(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        int bestSlot = 0;
        double bestSpeed = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) continue;
            float speed = stack.getDestroySpeed(state);
            int eff;
            if (speed > 0) {
                speed += ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? (Math.pow(eff, 2) + 1) : 0);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    public static void switchToSlot(int slot, boolean silent) {
        if(slot == -1) return;
        if (!silent) mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
        }
    }

    public static int findWeaponSlot(int min, int max, boolean shieldBreak) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if(shieldBreak) if(stack.getItem() instanceof ItemAxe) return i;
            else if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe) return i;
        }

        return -1;
    }

    public static float getDamageInPercent(ItemStack stack) {
        final float green = (stack.getMaxDamage() - ( float ) stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return ( float ) (100 - ( int ) (red * 100.0f));
    }

    public static int findItem(Item item, int min, int max) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            return i;
        }

        return -1;
    }

    public static int findBlock(Block block, int min, int max) {
        for (int i = min; i <= max; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!(stack.getItem() instanceof ItemBlock)) continue;
            ItemBlock item = (ItemBlock)stack.getItem();
            if (item.getBlock() != block) continue;
            return i;
        }
        return -1;
    }

    public static int findBlockExtended(Block block, int min, int max) {
        if(block instanceof ExtendedBlock) {
            ExtendedBlock extended = (ExtendedBlock) block;

            for(int i = min; i <= max; i++) if(extended.isIt(i)) return i;

            return -1;
        } else return findBlock(block, min, max);
    }

    public static int findBlockExtendedExclude(Block block, int min, int max, ExtendedBlock... excludes) {
        if(block instanceof ExtendedBlock) {
            ExtendedBlock extended = (ExtendedBlock) block;

            if(!Arrays.asList(excludes).contains(extended)) for (int i = min; i <= max; i++) if (extended.isIt(i)) return i;

            return -1;
        } else {
            for (int i = min; i <= max; i++) for(ExtendedBlock exclude : excludes) if(exclude.isIt(i)) return -1;

            return findBlock(block, min, max);
        }
    }

    public static void switchToSlotGhost(int slot) {
        if (slot != -1 && InventoryUtil.mc.player.inventory.currentItem != slot) InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }

    public static int getHotbarItemSlot(Item item) {
        for (int i = 0; i < 9; ++i) if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
        return -1;
    }

    public static int getBlockInHotbar(Block block) {
        for (int i = 0; i < 9; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();

            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block)) return i;
        }

        return -1;
    }

    public static int findItemInHotbar(Class<? extends Item> itemToFind) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for(int i = 0; i < 9; i++) {
            ItemStack stack = mainInventory.get(i);

            if(stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            if(itemToFind.isInstance(stack.getItem())) slot = i;
        }

        return slot;
    }

    public static int findPickInHotbar() {
        return findItemInHotbar(ItemPickaxe.class);
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mc.player.inventory.mainInventory.get(i);

            if (stack != ItemStack.EMPTY && itemToFind.isInstance(stack.getItem()) && itemToFind.isInstance(stack.getItem())) return i;
        }
        return -1;
    }

    public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            slots.add(i);
        }
        return slots;
    }

    //rerhack
    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            final ItemStack stack = player.inventory.armorInventory.get(i);
            if (getDamageInPercent(stack) < percent) return true;
        }
        return false;
    }
}
