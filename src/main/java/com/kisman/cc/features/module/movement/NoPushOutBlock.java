package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoPushOutBlock extends Module {

    public NoPushOutBlock(){
        super("NoPushOutBlock", Category.MOVEMENT, true);
    }

    @SubscribeEvent
    public void onPlayerPushOut(PlayerSPPushOutOfBlocksEvent event){
        event.setCanceled(true);
    }
}
