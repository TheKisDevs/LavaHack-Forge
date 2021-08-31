package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoEZ extends Module {
    public AutoEZ() {
        super("AutoEZ", "", Category.CHAT);

        //Kisman.instance.settingsManager.rSetting(new Setting("Log", this, false));
    }

//    public void update() {
//        //boolean ezLog = Kisman.instance.settingsManager.getSettingByName(this, "Log").getValBoolean();
//
//        mc.world.loadedEntityList.stream()
//            .filter(e -> e != mc.player)
//            .forEach(e -> {
//                if(e instanceof EntityPlayer) {
//                    if(e.isDead) {
//                        mc.player.sendChatMessage(e.getName() + "ez! " + Kisman.NAME + " " + Kisman.VERSION + " on top!");
//                    }
//                    // if(ezLog) {
//                    //     mc.player.sendChatMessage(e.getName() + " ez log, kisman.cc on top!");
//                    // }
//                }
//            }
//        );
//    }
     @SubscribeEvent
     public void onLivingDeathEvent(LivingDeathEvent event) {
         if(event.getEntity().isDead) {
             mc.player.sendChatMessage(event.getEntity().getName() + " ez! " + Kisman.NAME + " " + Kisman.VERSION + "on Top!");
         }
     }
}
