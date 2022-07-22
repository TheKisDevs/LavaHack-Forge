package com.kisman.cc.features.module.combat;

import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.entity.player.PlayerUtil;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.world.CrystalUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("SimplifyStreamApiCallChains")
public class OffHand extends Module {
    private final Setting health = register(new Setting("Health", this, 11, 0, 36, true));

    private final Setting mode = register(new Setting("Mode", this, "Totem", Arrays.asList("Totem", "Crystal", "Gap", "Pearl", "Chorus", "Strength", "Shield")));
    private final Setting fallingMode = register(new Setting("Falling Mode", this, "Totem", Arrays.asList("Totem", "Crystal", "Gap", "Pearl", "Chorus", "Strength", "Shield")));
    private final Setting fallDistance = register(new Setting("Fall Distance", this, 15, 0, 100, true));
    private final Setting totemOnElytra = register(new Setting("Totem On Elytra", this, true));
    private final Setting offhandGapOnSword = register(new Setting("Gap On Sword", this, true));
    private final Setting rightClickGap = register(new Setting("Right Click Gap", this, false));
    private final Setting totemIfNoNearbyPlayers = register(new Setting("Totem If No Nearby Players", this, false));
    private final Setting hotbarFirst = register(new Setting("Hotbar First", this, false));
    private final Setting useUpdateController = register(new Setting("Use UpdateController", this, true));
    private final Setting antiTotemFail = register(new Setting("Anti Totem Fail", this, true));
    private final Setting terrain = register(new Setting("Terrain", this, true));

    private final MultiThreaddableModulePattern threads = new MultiThreaddableModulePattern(this);

    private final AtomicBoolean needTotem = new AtomicBoolean(false);

    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    public void onEnable() {
        threads.reset();
    }

    public void update() {
        if(mc.player == null && mc.world == null && mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory))) return;

        threads.update(() -> {
            needTotem.set(totemIfNoNearbyPlayers.getValBoolean() && !mode.getValString().equalsIgnoreCase("Totem") && mc.world.playerEntities.stream().noneMatch(e -> !(e == mc.player || FriendManager.instance.isFriend(e) || mc.player.getDistance(e) > 15)));
            needTotem.set(needTotem.get() || (antiTotemFail.getValBoolean() && !mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) && mc.world.loadedEntityList.stream().anyMatch(e -> e instanceof EntityEnderCrystal && mc.player.getDistanceSq(e) <= (12 * 12) && CrystalUtils.calculateDamage(mc.world, e.posX + 0.5, e.posY, e.posZ + 0.5, mc.player, terrain.getValBoolean()) >= mc.player.getHealth() + mc.player.getAbsorptionAmount())));
        });

        String name = mode.getValString();

        if (needTotem.get() || health.getValDouble() > (mc.player.getHealth() + mc.player.getAbsorptionAmount()) || mode.getValString().equalsIgnoreCase("Totem") || (totemOnElytra.getValBoolean() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallDistance.getValDouble() && !mc.player.isElytraFlying())) name = "Totem";
        if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean()) || (rightClickGap.getValBoolean() && Mouse.isButtonDown(1) && !mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE))) name = "Gap";

        switchOffHandIfNeed(name);
    }

    private void switchOffHandIfNeed(String mode) {
        Item item = getItemFromModeVal(mode);
        Item fallback = getItemFromModeVal(fallingMode.getValString());

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = hotbarFirst.getValBoolean() ? PlayerUtil.GetRecursiveItemSlot(item) : PlayerUtil.GetItemSlot(item);

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = PlayerUtil.GetRecursiveItemSlot(fallback);

                if ((slot == -1 && fallback != Items.TOTEM_OF_UNDYING) || item != Items.TOTEM_OF_UNDYING && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) slot = PlayerUtil.GetRecursiveItemSlot(Items.TOTEM_OF_UNDYING);
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                if(useUpdateController.getValBoolean()) mc.playerController.updateController();
            }
        }
    }

    public Item getItemFromModeVal(String mode) {
        switch (mode) {
            case "Crystal": return Items.END_CRYSTAL;
            case "Gap": return Items.GOLDEN_APPLE;
            case "Pearl": return Items.ENDER_PEARL;
            case "Chorus": return Items.CHORUS_FRUIT;
            case "Strength": return Items.POTIONITEM;
            case "Shield": return Items.SHIELD;
            default: return Items.TOTEM_OF_UNDYING;
        }
    }
}