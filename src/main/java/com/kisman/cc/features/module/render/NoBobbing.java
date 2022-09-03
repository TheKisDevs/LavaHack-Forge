package com.kisman.cc.features.module.render;

import com.kisman.cc.event.events.EventApplyBobbing;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class NoBobbing extends Module {

    public NoBobbing(){
        super("NoBobbing", Category.RENDER, true);
    }

    @EventHandler
    private final Listener<EventApplyBobbing> listener = new Listener<>(Cancellable::cancel);
}
