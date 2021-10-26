package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class NoPitchLimit extends Module {
    public NoPitchLimit() {
        super("NoPitchLimit", "", Category.RENDER);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if(event.getEra() == Event.Era.PRE) {
            if(!(mc.player.rotationPitch >= 90)) {
                if(mc.player.rotationPitch <= -90) {
                    mc.player.rotationPitch = -90f;
                }
            }
        }
    });
}
