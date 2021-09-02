package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;

public class OffHand extends Module {
    private ArrayList<String> offHandMode = new ArrayList<>();

    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);

        offHandMode.add("Crystal");
        offHandMode.add("Totem");
        offHandMode.add("Gapple");

        Kisman.instance.settingsManager.rSetting(new Setting("OffHandMode", this, "Crystal", offHandMode));
        Kisman.instance.settingsManager.rSetting(new Setting("TotemLine", this, "Totem"));
        Kisman.instance.settingsManager.rSetting(new Setting("Totem", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("TotemHP", this, 10, 1, 20, true));
    }

    public void update() {
        int[] slots = new int[]{
                0,//totem
                0,//crystal
                0//gapple
        };

        NonNullList<ItemStack> inv;
        ItemStack offhand = Wrapper.INSTANCE.player().getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        int inventoryIndex;
        inv = Wrapper.INSTANCE.inventory().mainInventory;

        int totemHealth = (int) Kisman.instance.settingsManager.getSettingByName(this, "TotemHP").getValDouble();
        String offHandMode = Kisman.instance.settingsManager.getSettingByName(this, "OffHandMode").getValString();
        boolean totem = Kisman.instance.settingsManager.getSettingByName(this, "Totem").getValBoolean();

        if(mc.player == null && mc.world == null) {
            return;
        }

        for(inventoryIndex = 0; inventoryIndex < inv.size(); inventoryIndex++) {
            if(inv.get(inventoryIndex) != ItemStack.EMPTY) {
                if(inv.get(inventoryIndex).getItem() == Items.TOTEM_OF_UNDYING) {
                    slots[0] = inventoryIndex;
                }
                if(inv.get(inventoryIndex).getItem() == Items.END_CRYSTAL) {
                    slots[1] = inventoryIndex;
                }
                if(inv.get(inventoryIndex).getItem() == Items.GOLDEN_APPLE) {
                    slots[2] = inventoryIndex;
                }
            }
        }

        if((int) mc.player.getHealth() < totemHealth && totem) {
            if(slots[0] != 0) {
                mc.playerController.windowClick(0, slots[0], 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0,45,0, ClickType.PICKUP, mc.player);
            }
        } else if(mc.player.getHealth() >= totemHealth) {
            if(offHandMode.equalsIgnoreCase("Crystal") && slots[1] != 0) {
                mc.playerController.windowClick(0, slots[1], 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0,45,0, ClickType.PICKUP, mc.player);
            } else if(offHandMode.equalsIgnoreCase("Gapple") && slots[2] != 0) {
                mc.playerController.windowClick(0, slots[2], 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0,45,0, ClickType.PICKUP, mc.player);
            }
        }
    }
}
