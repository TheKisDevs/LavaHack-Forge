package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoEZ extends Module {
    public AutoEZ() {
        super("AutoEZ", "", Category.CHAT);
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if(event.getEntity().isDead) {
            mc.player.sendChatMessage(event.getEntity().getName() + " ez! ft. " + Kisman.NAME + " " + Kisman.VERSION);
        }
    }
}
