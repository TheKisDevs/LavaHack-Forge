package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.entity.player.PlayerUtil;
import com.kisman.cc.util.enums.OffhandItems;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.world.CrystalUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.concurrent.atomic.AtomicBoolean;

@PingBypassModule
@SuppressWarnings("SimplifyStreamApiCallChains")
public class OffHand extends Module {
    private final Setting health = register(new Setting("Health", this, 11, 0, 36, true));

    private final SettingEnum<OffhandItems> mode = new SettingEnum<>("Mode", this, OffhandItems.Totem).register();
    private final SettingEnum<OffhandItems> fallingMode = new SettingEnum<>("Falling Mode", this, OffhandItems.Totem).register();
    private final Setting fallDistance = register(new Setting("Fall Distance", this, 15, 0, 100, true));
    private final Setting antiPlace = register(new Setting("Anti Place", this, false));
//    private final Setting replacePacket = register(new Setting("AP Replace Packet", this, false));
    private final Setting totemOnElytra = register(new Setting("Totem On Elytra", this, true));
    private final Setting offhandGapOnSword = register(new Setting("Gap On Sword", this, true));
    private final Setting rightClickGap = register(new Setting("Right Click Gap", this, false).setVisible(offhandGapOnSword));
    private final Setting totemIfNoNearbyPlayers = register(new Setting("Totem If No Nearby Players", this, false));
    private final Setting hotbarFirst = register(new Setting("Hotbar First", this, false));
    private final Setting useUpdateController = register(new Setting("Use UpdateController", this, true));
    private final Setting antiTotemFail = register(new Setting("Anti Totem Fail", this, true));
    private final Setting terrain = register(new Setting("Terrain", this, true));
    public final Setting smartSwitchAutoRerSync = register(new Setting("Smart Switch Auto Rer Sync", this, false));

    private final MultiThreaddableModulePattern threads = threads();

    private final AtomicBoolean needTotem = new AtomicBoolean(false);

    @ModuleInstance
    public static OffHand instance;

    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    public void onEnable() {
        super.onEnable();
        threads.reset();
    }

    public void update() {
        if(mc.player == null || mc.world == null || (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory))) return;

        threads.update(() -> {
            needTotem.set(totemIfNoNearbyPlayers.getValBoolean() && !mode.getValString().equalsIgnoreCase("Totem") && mc.world.playerEntities.stream().noneMatch(e -> !(e == mc.player || FriendManager.instance.isFriend(e) || mc.player.getDistance(e) > 15)));
            needTotem.set(needTotem.get() || (antiTotemFail.getValBoolean() && !mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) && mc.world.loadedEntityList.stream().anyMatch(e -> e instanceof EntityEnderCrystal && mc.player.getDistanceSq(e) <= (12 * 12) && CrystalUtils.calculateDamage(mc.world, e.posX + 0.5, e.posY, e.posZ + 0.5, mc.player, terrain.getValBoolean()) >= mc.player.getHealth() + mc.player.getAbsorptionAmount())));
        });

        OffhandItems item = mode.getValEnum();

        if(!smartSwitchAutoRerSync.getValBoolean()) {
            if (needTotem.get() || health.getValDouble() > (mc.player.getHealth() + mc.player.getAbsorptionAmount()) || mode.getValString().equalsIgnoreCase("Totem") || (totemOnElytra.getValBoolean() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallDistance.getValDouble() && !mc.player.isElytraFlying())) item = OffhandItems.Totem;
            if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean() && !rightClickGap.getValBoolean()) || (offhandGapOnSword.getValBoolean() && rightClickGap.getValBoolean() && Mouse.isButtonDown(1) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) item = OffhandItems.Gap;
        } else if(AutoRer.instance.shouldSmartSwitch.get() && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) item = OffhandItems.Crystal;

        doOffHand(item.getItem());
    }
    
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(antiPlace.getValBoolean()) {
            if(mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood) {
                event.setCanceled(true);
            }
        }
    }

    private void doOffHand(Item item) {
        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = hotbarFirst.getValBoolean() ? PlayerUtil.GetRecursiveItemSlot(item) : PlayerUtil.GetItemSlot(item);

            if (slot == -1 && item != fallingMode.getValEnum().getItem() && mc.player.getHeldItemOffhand().getItem() != fallingMode.getValEnum().getItem()) {
                slot = PlayerUtil.GetRecursiveItemSlot(fallingMode.getValEnum().getItem());

                if ((slot == -1 && fallingMode.getValEnum().getItem() != Items.TOTEM_OF_UNDYING) || item != Items.TOTEM_OF_UNDYING && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) slot = PlayerUtil.GetRecursiveItemSlot(Items.TOTEM_OF_UNDYING);
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                if(useUpdateController.getValBoolean()) mc.playerController.updateController();
            }
        }
    }
}