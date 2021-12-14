package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.Arrays;

public class SilentXp extends Module {
    private Setting lookPitch = new Setting("LookPitch", this, 90, 0, 100, false);
    private Setting switchMode = new Setting("SwitchMode", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Client")));

    private int delayCount;
    private int prvSlot;

    public SilentXp() {
        super("SilentXP", "SilentXp", Category.COMBAT);

        setmgr.rSetting(lookPitch);
        setmgr.rSetting(switchMode);
    }

    public void onEnable() {
        delayCount = 0;
    }

    public void update() {
        if(mc.currentScreen == null && mc.player != null && mc.world != null) {
            usedXp();
        }
    }

    private int findExpInHotbar() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private void usedXp() {
        int oldPitch = (int)mc.player.rotationPitch;
        prvSlot = mc.player.inventory.currentItem;

        switch (switchMode.getValString()) {
            case "Packet": {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(findExpInHotbar()));
                break;
            }
            case "Client": {
                mc.player.inventory.currentItem = findExpInHotbar();
                break;
            }
        }

        mc.player.rotationPitch = (float) lookPitch.getValDouble();
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, (float) lookPitch.getValDouble(), true));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.rotationPitch = oldPitch;

        switch (switchMode.getValString()) {
            case "Packet": {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
                break;
            }
            case "Client": {
                mc.player.inventory.currentItem = prvSlot;
                break;
            }
        }
    }
}
