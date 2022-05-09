package com.kisman.cc.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventBlockPlaceCheck;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.Listener;

/**
 * Don't add or use
 */
public class BlockPlaceCheck extends Module {

    public BlockPlaceCheck(){
        super("BlockPlaceCheck", Category.DEBUG);
    }

    @Override
    public void onEnable(){
        Kisman.EVENT_BUS.subscribe(listener);
    }

    private final Listener<EventBlockPlaceCheck> listener = new Listener<>(event -> {
        if(mc.player == null || mc.world == null)
            return;

        ChatUtility.message().printClientModuleMessage("checking block placement...");
    });

    @Override
    public void onDisable(){
        Kisman.EVENT_BUS.unsubscribe(listener);
    }
}
