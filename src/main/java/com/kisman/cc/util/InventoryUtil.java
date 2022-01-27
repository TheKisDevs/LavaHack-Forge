package com.kisman.cc.util;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

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

    public static boolean isArmorLow(final EntityPlayer player, final int durability) {
        for (int i = 0; i < 4; ++i) if (getDamageInPercent(player.inventory.armorInventory.get(i)) < durability) return true;

        return false;
    }

    public static float getDamageInPercent(final ItemStack stack) {
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

    public static int findAntiWeaknessTool() {
        return findAntiWeaknessTool(0, 9);
    }

    public static int findAntiWeaknessTool(int min, int max) {
        for(int i = min; i <= max; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemPickaxe) return i;
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

    public static void switchToSlot(int slot, Switch switchMode) {
        if(mc.player == null) return;

        if (slot != -1 && mc.player.inventory.currentItem != slot) {
            switch (switchMode) {
                case NORMAL:
                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    break;
                case PACKET:
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    break;
            }
        }

        mc.playerController.updateController();
//        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static void switchToSlot(Item item, Switch switchMode) {
        if (getItemSlot(item, Inventory.HOTBAR, true) != -1 && mc.player.inventory.currentItem != getItemSlot(item, Inventory.HOTBAR, true))
            switchToSlot(getItemSlot(item, Inventory.HOTBAR, true), switchMode);

//        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static int getBlockInHotbar(boolean onlyObby) {
        for(int i = 0; i <9; i++) if(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) return i;
        return -1;
    }

    public static int getItemSlot(Item item, Inventory inventory, boolean hotbar) {
        switch (inventory) {
            case HOTBAR:
                for (int i = 0; i < 9; i++) if (mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
                break;
            case INVENTORY:
                for (int i = hotbar ? 9 : 0; i < 45; i++) if (mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
                break;
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

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slot = i;
                break;
            }
        }
        return slot;
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

    public static List<Integer> findAllItemSlots(Item itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            if (itemToFind != mainInventory.get(i).item) continue;
            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slots.add(i);
            }
        }
        return slots;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();

        result = isInstanceOf(stack, clazz);

        return result;
    }

    //rerhack
    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            final ItemStack stack = player.inventory.armorInventory.get(i);
            if (getDamageInPercent(stack) < percent) return true;
        }
        return false;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    //zero two
    public static
    boolean isInstanceOf ( ItemStack stack , Class clazz ) {
        if ( stack == null ) {
            return false;
        }

        Item item = stack.getItem ( );
        if ( clazz.isInstance ( item ) ) {
            return true;
        }

        if ( item instanceof ItemBlock ) {
            Block block = Block.getBlockFromItem ( item );
            return clazz.isInstance ( block );
        }

        return false;
    }

    public enum Switch {
        NORMAL, PACKET, NONE
    }

    public enum Inventory {
        INVENTORY, HOTBAR, CRAFTING
    }
}
