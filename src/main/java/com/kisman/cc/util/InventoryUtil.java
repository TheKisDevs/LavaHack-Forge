package com.kisman.cc.util;

import com.kisman.cc.mixin.mixins.accessor.IPlayerControllerMP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void switchToSlot(int slot, Switch switchMode) {
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
        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static void switchToSlot(Item item, Switch switchMode) {
        if (getItemSlot(item, Inventory.HOTBAR, true) != -1 && mc.player.inventory.currentItem != getItemSlot(item, Inventory.HOTBAR, true))
            switchToSlot(getItemSlot(item, Inventory.HOTBAR, true), switchMode);

        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static int getItemSlot(Item item, Inventory inventory, boolean hotbar) {
        switch (inventory) {
            case HOTBAR:
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == item)
                        return i;
                }

                break;
            case INVENTORY:
                for (int i = hotbar ? 9 : 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == item)
                        return i;
                }

                break;
        }

        return -1;
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

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

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

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

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

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
