package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.Arrays;

public class OffHand extends Module {
    private Setting health = new Setting("Health", this, 11, 0, 20, true);

    private Setting mode = new Setting("Mode", this, "Totem", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private Setting fallBackMode = new Setting("FallBackMode", this, "Crystal", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private Setting fallBackDistance = new Setting("FallBackDistance", this, 15, 0, 100, true);
    private Setting totemOnElytra = new Setting("TotemOnElytra", this, true);
    private Setting offhandGapOnSword = new Setting("OffhandGapOnSword", this, true);
    private Setting hotbarFirst = new Setting("HotbarFirst", this, false);


    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);

        setmgr.rSetting(health);
        setmgr.rSetting(mode);
        setmgr.rSetting(fallBackMode);
        setmgr.rSetting(fallBackDistance);
        setmgr.rSetting(totemOnElytra);
        setmgr.rSetting(offhandGapOnSword);
        setmgr.rSetting(hotbarFirst);
    }

    private void SwitchOffHandIfNeed(String mode)
    {
        Item l_Item = GetItemFromModeVal(mode);

        if (mc.player.getHeldItemOffhand().getItem() != l_Item)
        {
            int l_Slot = hotbarFirst.getValBoolean() ? PlayerUtil.GetRecursiveItemSlot(l_Item) : PlayerUtil.GetItemSlot(l_Item);

            Item l_Fallback = GetItemFromModeVal(fallBackMode.getValString());

            String l_Display = GetItemNameFromModeVal(mode);

            if (l_Slot == -1 && l_Item != l_Fallback && mc.player.getHeldItemOffhand().getItem() != l_Fallback)
            {
                l_Slot = PlayerUtil.GetRecursiveItemSlot(l_Fallback);
                l_Display = GetItemNameFromModeVal(fallBackMode.getValString());

                /// still -1...
                if (l_Slot == -1 && l_Fallback != Items.TOTEM_OF_UNDYING)
                {
                    l_Fallback = Items.TOTEM_OF_UNDYING;

                    if (l_Item != l_Fallback && mc.player.getHeldItemOffhand().getItem() != l_Fallback)
                    {
                        l_Slot = PlayerUtil.GetRecursiveItemSlot(l_Fallback);
                        l_Display = "Emergency Totem";
                    }
                }
            }

            if (l_Slot != -1)
            {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_Slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP,
                        mc.player);

                /// @todo: this might cause desyncs, we need a callback for windowclicks for transaction complete packet.
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_Slot, 0,
                        ClickType.PICKUP, mc.player);
                mc.playerController.updateController();

                ChatUtils.complete(ChatFormatting.YELLOW + "[AutoTotem] " + ChatFormatting.LIGHT_PURPLE + "Offhand now has a " + l_Display);
            }
        }
    }

    public void update() {
        if (mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory)))
            return;

        if (!mc.player.getHeldItemMainhand().isEmpty())
        {
            if (health.getValDouble() <= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean() && !mc.player.isPotionActive(MobEffects.STRENGTH))
            {
                SwitchOffHandIfNeed("Strength");
                return;
            }

            /// Sword override
            if (health.getValDouble() <= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean())
            {
                SwitchOffHandIfNeed("Gapple");
                return;
            }
        }

        /// First check health, most important as we don't want to die for no reason.
        if (health.getValDouble() > (mc.player.getHealth() + mc.player.getAbsorptionAmount()) || mode.getValString().equalsIgnoreCase("Totem") || (totemOnElytra.getValBoolean() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallBackDistance.getValDouble() && !mc.player.isElytraFlying()) || noNearbyPlayers())
        {
            SwitchOffHandIfNeed("Totem");
            return;
        }

        /// If we meet the required health

        SwitchOffHandIfNeed(mode.getValString());
    }

    public Item GetItemFromModeVal(String mode) {
        switch (mode) {
            case "Crystal":
                return Items.END_CRYSTAL;
            case "Gap":
                return Items.GOLDEN_APPLE;
            case "Pearl":
                return Items.ENDER_PEARL;
            case "Chorus":
                return Items.CHORUS_FRUIT;
            case "Strength":
                return Items.POTIONITEM;
            case "Shield":
                return Items.SHIELD;
            default:
                break;
        }

        return Items.TOTEM_OF_UNDYING;
    }

    private String GetItemNameFromModeVal(String mode) {
        switch (mode) {
            case "Crystal":
                return "End Crystal";
            case "Gap":
                return "Gap";
            case "Pearl":
                return "Pearl";
            case "Chorus":
                return "Chorus";
            case "Strength":
                return "Strength";
            case "Shield":
                return "Shield";
            default:
                break;
        }

        return "Totem";
    }

    private boolean noNearbyPlayers() {
        if (mode.getValString().equalsIgnoreCase("Crystal") && mc.world.playerEntities.stream().noneMatch(e -> e != mc.player && isValidTarget(e))) {
            return true;
        }
        return false;
    }

    private boolean isValidTarget(Entity p_Entity) {
        if (p_Entity == mc.player) {
            return false;
        }
        if (mc.player.getDistance(p_Entity) > 15) {
            return false;
        }
        return true;
    }
}
