package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;

/**
 * Don't add or use
 */
public class BlockPlaceCheck extends Module {

    public BlockPlaceCheck(){
        super("BlockPlaceCheck", Category.DEBUG);
    }

    /*
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
     */
}
