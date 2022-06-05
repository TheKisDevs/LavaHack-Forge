package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventOrientCamera;
import com.kisman.cc.features.module.*;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class CameraClip extends Module {
    public static CameraClip instance;

    public CameraClip() {
        super("CameraClip", Category.RENDER);

        instance = this;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(orientCamera);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(orientCamera);
    }

    @EventHandler private final Listener<EventOrientCamera> orientCamera = new Listener<>(event -> {
        event.cancel();
    });
}