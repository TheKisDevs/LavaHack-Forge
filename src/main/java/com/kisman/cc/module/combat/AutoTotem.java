package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", "simple offhand", Category.COMBAT);

        Kisman.instance.settingsManager.rSetting(new Setting("Health", this, 10, 1, 20, true));
    }

    public void update() {
        if(mc.player == null && mc.world == null) {
            return;
        }

        int totemSlot = 0;

        NonNullList<ItemStack> inv;
        ItemStack offhand = Wrapper.INSTANCE.player().getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        int inventoryIndex;
        inv = Wrapper.INSTANCE.inventory().mainInventory;

        int health = (int) Kisman.instance.settingsManager.getSettingByName(this, "Health").getValDouble();

        for(inventoryIndex = 0; inventoryIndex < inv.size(); inventoryIndex++) {
            if(inv.get(inventoryIndex) != ItemStack.EMPTY) {
                if(inv.get(inventoryIndex).getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSlot = inventoryIndex;
                }
            }
        }

        if((int) mc.player.getHealth() < health) {
            if(offhand == null || offhand.getItem() == Items.TOTEM_OF_UNDYING) {
                return;
            }

            if(totemSlot != 0) {
                mc.playerController.windowClick(0, totemSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0,45,0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, totemSlot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }
}
