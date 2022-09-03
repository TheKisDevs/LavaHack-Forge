package com.kisman.cc.features.module.misc;

import com.kisman.cc.event.events.EventServerPing;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class NoPing extends Module {

    private final Setting compatibility = register(new Setting("Compatibility", this, false));

    public NoPing(){
        super("NoPing", Category.MISC, true);
    }

    @EventHandler
    private final Listener<EventServerPing.Normal> normalListener = new Listener<>(Cancellable::cancel);

    @EventHandler
    private final Listener<EventServerPing.Compatibility> compatibilityListener = new Listener<>(event -> {
        if(compatibility.getValBoolean())
            event.cancel();
    });
}
