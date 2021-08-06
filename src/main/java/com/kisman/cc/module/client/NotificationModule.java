package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.notification.Notification;
import com.kisman.cc.notification.NotificationType;

public class NotificationModule extends Module {
    Notification notification;

    public NotificationModule() {
        super("Notification", "Notification", Category.CLIENT);
    }

    public void render() {
//        notification = new com.kisman.cc.notification.Notification(NotificationType.INFO, "test title", "test message", 100, 100, 100, 90, 40);
//        notification.render();
    }
    public void update() {
        Kisman.setNotificatonModule(true);
    }
}
