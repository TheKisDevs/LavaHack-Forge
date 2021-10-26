package com.kisman.cc.hud.hudmodule.misc;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.oldclickgui.notification.NotificationManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NotificationHud extends HudModule {
    public static NotificationHud instance;

    public NotificationHud() {
        super("NotificationHud", "", HudCategory.MISC);

        instance = this;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        NotificationManager.render();
    }
}
