package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventApplyBobbing;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class NoBobbing extends Module {

    public NoBobbing(){
        super("NoBobbing", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null)
            return;
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventApplyBobbing> listener = new Listener<>(Cancellable::cancel);
}
