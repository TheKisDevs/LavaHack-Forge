package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
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

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    private final Listener<EventServerPing.Normal> normalListener = new Listener<>(Cancellable::cancel);

    @EventHandler
    private final Listener<EventServerPing.Compatibility> compatibilityListener = new Listener<>(event -> {
        if(compatibility.getValBoolean())
            event.cancel();
    });
}
