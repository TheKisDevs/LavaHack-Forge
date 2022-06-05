package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NotificationsModule extends HudModule {
    public NotificationsModule() {
        super("Notifications");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
//        Kisman.instance.notificationsManager.draw();
    }
}
