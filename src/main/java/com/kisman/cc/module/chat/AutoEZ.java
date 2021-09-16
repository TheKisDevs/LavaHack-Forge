package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoEZ extends Module {
    String[] no_team = new String[] {
            "mudonna",
            "magisteroff",
            "momkilla",
            "ebatte_sratte",
            "azazel",
            "tem4ik"
    };

    public AutoEZ() {
        super("AutoEZ", "", Category.CHAT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

     @SubscribeEvent
     public void onLivingDeathEvent(LivingDeathEvent event) {
         if(event.getEntity().isDead) {
             for(int i = 0; i < no_team.length; i++) {
                 if(event.getEntity().getName().equalsIgnoreCase(no_team[i])) {
                     mc.player.sendChatMessage("I fuck NO team member " + no_team[i] + " and all NO team! | kisman.cc on top!");
                     return;
                 }
             }

             mc.player.sendChatMessage(event.getEntity().getName() + " ez! " + Kisman.NAME + " " + Kisman.VERSION + "on Top!");
         }
     }
}
