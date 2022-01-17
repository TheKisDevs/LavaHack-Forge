package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;

import com.kisman.cc.module.combat.AutoRer;
import net.minecraft.entity.player.EntityPlayer;

public class AutoEZ extends Module {
    private EntityPlayer currentIgnoredTarget;
    public AutoEZ() {
        super("AutoEZ", "", Category.CHAT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(AutoRer.currentTarget == null) currentIgnoredTarget = null;
        else if(AutoRer.currentTarget.isDead && AutoRer.currentTarget != currentIgnoredTarget && AutoRer.instance.isToggled()) mc.player.sendChatMessage(AutoRer.currentTarget.getName() + " owned by " + Kisman.getName() + "!");
    }
}
