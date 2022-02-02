 package com.kisman.cc.module.combat;

 import com.kisman.cc.Kisman;
 import com.kisman.cc.module.*;
 import com.kisman.cc.util.InventoryUtil;
 import com.kisman.cc.settings.*;

 import net.minecraft.client.gui.inventory.GuiContainer;
 import net.minecraft.client.renderer.InventoryEffectRenderer;
 import net.minecraft.enchantment.*;
 import net.minecraft.init.Items;
 import net.minecraft.inventory.ClickType;
 import net.minecraft.item.*;

 import java.util.*;

 public class AutoArmor extends Module{
     public static AutoArmor instance;

     public AutoArmor() {
         super("AutoArmor", "ebate srate lox!", Category.COMBAT);

         instance = this;

         Kisman.instance.settingsManager.rSetting(new Setting("NoThorns", this, false));
     }

     public void update() {
         if(mc.player == null && mc.world == null) return;

         boolean noThorns = Kisman.instance.settingsManager.getSettingByName(this, "NoThorns").getValBoolean();

         if (mc.player.ticksExisted % 2 == 0) return;
         // check screen
         if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof InventoryEffectRenderer)) return;

         List<ItemStack> armorInventory = mc.player.inventory.armorInventory;
         List<ItemStack> inventory = mc.player.inventory.mainInventory;

         // store slots and values of best armor pieces
         int[] bestArmorSlots = {-1, -1, -1, -1};
         int[] bestArmorValues = {-1, -1, -1, -1};

         // initialize with currently equipped armour
         for (int i = 0; i < 4; i++) {
             ItemStack oldArmour = armorInventory.get(i);
             if (oldArmour.getItem() instanceof ItemArmor) bestArmorValues[i] = ((ItemArmor) oldArmour.getItem()).damageReduceAmount;
         }

         List<Integer> slots = InventoryUtil.findAllItemSlots(ItemArmor.class);
         HashMap<Integer, ItemStack> armour = new HashMap<>();
         HashMap<Integer, ItemStack> thorns = new HashMap<>();

         for (Integer slot : slots) {
             ItemStack item = inventory.get(slot);
             // 7 is the id for thorns
             if (noThorns && EnchantmentHelper.getEnchantments(item).containsKey(Enchantment.getEnchantmentByID(7))) thorns.put(slot, item);
             else armour.put(slot, item);
         }

         armour.forEach(((integer, itemStack) -> {
             ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
             int armorType = itemArmor.armorType.ordinal() - 2;
             if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).getItem().equals(Items.ELYTRA)) return;
             int armorValue = itemArmor.damageReduceAmount;
             if (armorValue > bestArmorValues[armorType]) {
                 bestArmorSlots[armorType] = integer;
                 bestArmorValues[armorType] = armorValue;
             }
         }));

         if (noThorns) {
             thorns.forEach(((integer, itemStack) -> {
                 ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
                 int armorType = itemArmor.armorType.ordinal() - 2;

                 // Thorns Is only put in when all other is lost
                 if (!(armorInventory.get(armorType) == ItemStack.EMPTY && bestArmorSlots[armorType] == -1)) return;
                 if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).getItem().equals(Items.ELYTRA)) return;
                 int armorValue = itemArmor.damageReduceAmount;
                 if (armorValue > bestArmorValues[armorType]) {
                     bestArmorSlots[armorType] = integer;
                     bestArmorValues[armorType] = armorValue;
                 }
             }));
         }

         // equip better armor
         for (int i = 0; i < 4; i++) {
             // check if better armor was found
             int slot = bestArmorSlots[i];
             if (slot == -1) continue;
             // hotbar fix
             if (slot < 9) slot += 36;
             // pick up inventory slot
             mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
             // click on armour slot
             mc.playerController.windowClick(0, 8 - i, 0, ClickType.PICKUP, mc.player);
             // put back inventory slot
             mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
         }
     }
 }
