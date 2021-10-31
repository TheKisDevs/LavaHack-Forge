package com.kisman.cc.oldclickgui.notification;

import com.kisman.cc.module.client.HUD;

import java.util.ArrayList;

public class NotificationProcessor {
    public ArrayList<Notification> notifications = new ArrayList();

    public void handleNotifications(int posY) {
        for (int i = 0; i < this.getNotifications().size(); ++i) {
            if (this.getNotifications().get((int)i).animationUtils2.isDone() && !this.getNotifications().get((int)i).didThing) {
                this.getNotifications().get((int)i).animationUtils.reset();
                this.getNotifications().get((int)i).didThing = true;
            }
            if (this.getNotifications().get((int)i).animationUtils.isDone() && !this.getNotifications().get((int)i).isReversing && this.getNotifications().get((int)i).timer.hasReached(this.getNotifications().get((int)i).disableTime - (long)(HUD.instance.inOutTime.getValInt() * 2))) {
                this.getNotifications().get((int)i).reverse.reset();
                this.getNotifications().get((int)i).reverse2.reset();
                this.getNotifications().get((int)i).isReversing = true;
            }
            if (this.getNotifications().get((int)i).isReversing && this.getNotifications().get((int)i).reverse.isDone() && !this.getNotifications().get((int)i).didFirstReverse) {
                this.getNotifications().get((int)i).reverse2.reset();
                this.getNotifications().get((int)i).didFirstReverse = true;
            }
            this.getNotifications().get(i).onDraw((int)posY);
            if (HUD.instance.addType.getValBoolean()) {
                posY += 22;
                continue;
            }
            posY -= 22;
        }
    }

    public void addNotification(String text, long inOutTime, long duration) {
        this.getNotifications().add(new Notification(text, duration, inOutTime));
    }

    public ArrayList<Notification> getNotifications() {
        return this.notifications;
    }
}

