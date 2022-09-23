package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventStopUsingItem;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.player.InventoryUtil;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * @author Cubic
 * @since 23.09.2022
 * Inspired by the Quiver of Catalyst
 */
public class AutoQuiver extends Module {

    private final SettingEnum<ArrowMode> arrow = new SettingEnum<>("Arrow", this, ArrowMode.Swiftness).register();
    private final SettingEnum<Swap> swap = new SettingEnum<>("Switch", this, Swap.SwitchBack).register();
    private final SettingEnum<DelayMode> delay = new SettingEnum<>("Delay", this, DelayMode.None).register();
    private final Setting delayMS = register(new Setting("DelayMS", this, 2500, 0, 10000, true).setVisible(() -> delay.getValEnum() == DelayMode.Millis));
    private final Setting delayTicks = register(new Setting("DelayTicks", this, 50, 0, 200, true).setVisible(() -> delay.getValEnum() == DelayMode.Ticks));
    private final Setting useTicks = register(new Setting("UseTicks", this, 3, 1, 10, true));
    private final Setting auto = register(new Setting("Automatic", this, false));
    private final Setting arrange = register(new Setting("Arrange", this, true));
    private final Setting health = register(new Setting("Health", this, 12, 0, 36, true));
    private final Setting toggleOnComplete = register(new Setting("ToggleOnComplete", this, false));
    private final Setting animateRotation = register(new Setting("AnimateRotation", this, false));

    public AutoQuiver(){
        super("AutoQuiver", Category.COMBAT);
    }

    private final TimerUtils timer = new TimerUtils();

    private boolean cancel = false;

    private int oldSlot = -1;

    @Override
    public void onEnable(){
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @Override
    public void onDisable(){
        super.onDisable();
        if(oldSlot != -1)
            swap(oldSlot, true);
        cancel = false;
        oldSlot = -1;
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    private final Listener<EventStopUsingItem> listener = new Listener<>(event -> {
        if(!event.getPlayer().equals(mc.player))
            return;
        if(cancel)
            event.cancel();
    });

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        int ms = delay.getValEnum() == DelayMode.None ? 0 : (delay.getValEnum() == DelayMode.Millis ? delayMS.getValInt() : delayTicks.getValInt() * 50);
        if(ms != 0 && !timer.passedMillis(ms))
            return;

        if(mc.player.getHealth() + mc.player.getAbsorptionAmount() < health.getValInt())
            return;

        int bowSlot = InventoryUtil.getHotbarItemSlot(Items.BOW);
        if(bowSlot == -1){
            if(toggleOnComplete.getValBoolean()){
                ChatUtility.warning().printClientModuleMessage("You have no bow! Disabling...");
                toggle();
            }
            return;
        }

        if(!checkArrow())
            return;

        oldSlot = mc.player.inventory.currentItem;

        boolean toggleOff = false;

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, mc.player.onGround));
        if(animateRotation.getValBoolean()){
            mc.player.lastReportedYaw = 0;
            mc.player.lastReportedPitch = -90;
        }
        if(mc.player.getItemInUseMaxCount() >= useTicks.getValInt()){
            cancel = false;
            mc.playerController.onStoppedUsingItem(mc.player);
            timer.reset();
            swap(oldSlot, true);
            if(toggleOnComplete.getValBoolean())
                toggleOff = true;
        } else if(mc.player.getItemInUseMaxCount() >= 0){
            swap(bowSlot, false);
            if(!(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() instanceof ItemBow)){
                if(toggleOnComplete.getValBoolean())
                    toggle();
                return;
            }
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            cancel = true;
        }

        if(toggleOff)
            toggle();
    }

    private void swap(int slot, boolean switchBack){
        switch(swap.getValEnum()){
            case Off:
                break;
            case Vanilla:
                if(switchBack)
                    return;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
            case SwitchBack:
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
        }
    }

    private boolean checkArrow(){
        if(!hasPlayerArrow())
            return false;
        if(arrow.getValEnum() == ArrowMode.Any)
            return true;
        String arrowName = "Arrow of " + arrow.getValEnum().name();
        for(int i = 0; i < 36; i++){
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.getItem() != Items.TIPPED_ARROW)
                continue;
            if(itemStack.getDisplayName().equalsIgnoreCase(arrowName))
                return true;
            else if(arrange.getValBoolean())
                return arrange(i, arrowName);
            else
                return false;
        }
        ItemStack itemStack = mc.player.inventory.getStackInSlot(54);
        if(itemStack.getItem() != Items.TIPPED_ARROW)
            return false;
        if(itemStack.getDisplayName().equalsIgnoreCase(arrowName))
            return true;
        else if(arrange.getValBoolean())
            return arrange(45, arrowName);
        return false;
    }

    private boolean arrange(int slot, String name){
        for(int i = 0; i < 36; i++){
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.getItem() != Items.TIPPED_ARROW)
                continue;
            if(!itemStack.getDisplayName().equalsIgnoreCase(name))
                continue;
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            return true;
        }
        return false;
    }

    private boolean hasPlayerArrow(){
        for(int i = 0; i < 36; i++){
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.isEmpty())
                continue;
            if(itemStack.getItem() == Items.ARROW)
                return true;
        }
        ItemStack itemStack = mc.player.inventory.getStackInSlot(45);
        if(itemStack.isEmpty())
            return false;
        return itemStack.getItem() == Items.ARROW;
    }

    private enum DelayMode {
        None,
        Millis,
        Ticks
    }

    private enum Swap {
        Off,
        Vanilla,
        SwitchBack
    }

    private enum ArrowMode {
        Any,
        Swiftness,
        Strength,
        Regeneration,
        Healing
    }
}
