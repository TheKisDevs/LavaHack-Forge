package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class YCorrect extends Module {

    public YCorrect(){
        super("YCorrect", Category.DEBUG);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    private final Listener<EventPlayerMove> listener = new Listener<>(event -> {
        if(event.y <= -0.0784000015258789)
            event.y = 0;
    });
}
