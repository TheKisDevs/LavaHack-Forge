package com.kisman.cc.module.player;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

public class Swing extends Module {
    private Setting mode = new Setting("Mode", this, Hand.MAINHAND);

    public Swing() {
        super("Swing", "swing", Category.PLAYER);

        setmgr.rSetting(mode);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(mode.getValEnum().equals(Hand.MAINHAND)) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        } else if(mode.getValEnum().equals(Hand.OFFHAND)) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        } else if(mode.getValEnum().equals(Hand.PACKETSWING) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }

    public enum Hand {
        OFFHAND,
        MAINHAND,
        PACKETSWING
    }
}
