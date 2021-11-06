package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;

import java.util.ArrayList;
import java.util.Arrays;

public class OffHand extends Module {
    public static OffHand instance;

    private Setting health = new Setting("Health", this, 11, 0, 20, true);

    private Setting mode = new Setting("Mode", this, "Totem", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private Setting fallBackMode = new Setting("FallBackMode", this, "Crystal", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private Setting fallBackDistance = new Setting("FallBackDistance", this, 15, 0, 100, true);
    private Setting totemOnElytra = new Setting("TotemOnElytra", this, true);
    private Setting offhandGapOnSword = new Setting("OffhandGapOnSword", this, true);
    private Setting hotbarFirst = new Setting("HotbarFirst", this, false);


    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);
        super.setDisplayInfo("[" + mode.getValString() + "]");

        instance = this;

        setmgr.rSetting(health);
        setmgr.rSetting(mode);
        setmgr.rSetting(fallBackMode);
        setmgr.rSetting(fallBackDistance);
        setmgr.rSetting(totemOnElytra);
        setmgr.rSetting(offhandGapOnSword);
        setmgr.rSetting(hotbarFirst);
    }

    private void switchOffHandIfNeed(String mode) {
        Item item = getItemFromModeVal(mode);

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = hotbarFirst.getValBoolean() ? PlayerUtil.GetRecursiveItemSlot(item) : PlayerUtil.GetItemSlot(item);

            Item fallback = getItemFromModeVal(fallBackMode.getValString());

            String display = getItemNameFromModeVal(mode);

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = PlayerUtil.GetRecursiveItemSlot(fallback);
                display = getItemNameFromModeVal(fallBackMode.getValString());

                if (slot == -1 && fallback != Items.TOTEM_OF_UNDYING) {
                    fallback = Items.TOTEM_OF_UNDYING;

                    if (item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                        slot = PlayerUtil.GetRecursiveItemSlot(fallback);
                        display = "Emergency Totem";
                    }
                }
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();

                ChatUtils.complete(ChatFormatting.YELLOW + "[AutoTotem] " + ChatFormatting.LIGHT_PURPLE + "Offhand now has a " + display);
            }
        }
    }

    public void update() {
        if (mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory)))
            return;

        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            if (health.getValDouble() <= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                switchOffHandIfNeed("Strength");
                return;
            }

            if (health.getValDouble() <= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean()) {
                switchOffHandIfNeed("Gapple");
                return;
            }
        }

        if (health.getValDouble() > (mc.player.getHealth() + mc.player.getAbsorptionAmount()) || mode.getValString().equalsIgnoreCase("Totem") || (totemOnElytra.getValBoolean() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallBackDistance.getValDouble() && !mc.player.isElytraFlying()) || noNearbyPlayers()) {
            switchOffHandIfNeed("Totem");
            return;
        }
        switchOffHandIfNeed(mode.getValString());
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player) {
            return false;
        }

        if (mc.player.getDistance(entity) > 15) {
            return false;
        }

        return true;
    }

    public Item getItemFromModeVal(String mode) {
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

    private String getItemNameFromModeVal(String mode) {
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
}
