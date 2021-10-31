package com.kisman.cc.hud.hudmodule.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.client.HUD;
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
        block5: {
            block4: {
                if (mc.player == null) break block4;
                if (mc.world != null) break block5;
            }
            return;
        }
        if (Kisman.instance.notificationProcessor.notifications.size() > HUD.instance.max.getValInt()) {
            Kisman.instance.notificationProcessor.notifications.remove(0);
        }
        Kisman.instance.notificationProcessor.handleNotifications(HUD.instance.height.getValInt());
    }
}
