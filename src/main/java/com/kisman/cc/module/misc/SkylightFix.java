package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventUpdateLightmap;
import com.kisman.cc.module.*;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class SkylightFix extends Module {
    public static SkylightFix instance;

    public SkylightFix() {
        super("SkylightFix", Category.MISC);

        instance = this;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(updateLightmap);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(updateLightmap);
    }

    @EventHandler private final Listener<EventUpdateLightmap.Pre> updateLightmap = new Listener<>(Cancellable::cancel);
}
