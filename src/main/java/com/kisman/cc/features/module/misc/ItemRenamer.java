package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRenamer extends Module {

    public ItemRenamer(){
        super("ItemRenamer", Category.MISC);
    }

    public static final Map<Item, String> NAME_MAP = new ConcurrentHashMap<>();

    public static final Map<Item, String> ORIGINALS = new ConcurrentHashMap<>();

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        for(ItemStack stack : mc.player.inventoryContainer.getInventory()) {
            if(stack == null)
                continue;
            if(stack.isEmpty)
                continue;
            ORIGINALS.computeIfAbsent(stack.item, name -> stack.getDisplayName());
            if(NAME_MAP.get(stack.item) == null)
                continue;
            stack.setStackDisplayName(NAME_MAP.get(stack.item));
        }
    }
}
