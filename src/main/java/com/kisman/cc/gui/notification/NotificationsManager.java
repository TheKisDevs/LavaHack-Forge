package com.kisman.cc.gui.notification;

import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.Icons;

import java.awt.*;
import java.util.ArrayList;

public class NotificationsManager {
    public ArrayList<Notifications> notifications = new ArrayList<>();

    public void drawNotification(Notifications notification, int i){
        int x = (Render2DUtil.instance.width - 100);
        if(notification.getTime() <= 1000) x = (int) (Render2DUtil.instance.width - notification.getTime() / 10);
        else if(notification.getTime() >= 5000) x = (int)  ((5000 - (Render2DUtil.instance.width - notification.getTime())) / 10);
        int y = Render2DUtil.instance.height - 35 - (i * 30);
        Render2DUtil.drawSmoothRect(x, y, x + 100, y + 29, new Color(30, 30, 30, 150).getRGB());
        Render2DUtil.drawSmoothRect(x, y, x + 100 - notification.getTime() / 50, y + 2, notification.getColor().getRGB());
        CustomFontUtil.drawStringWithShadow(notification.getTitle(), x, y + 4, Color.white.getRGB());
        CustomFontUtil.drawStringWithShadow(notification.getMessage(), x, y + 15, Color.white.getRGB());
        Icons.CHECKED_CHECKBOX.render(x + 80, y + 5, 20, 20);
    }

    public void draw(){
        for(int i = 0; i < notifications.size() - 1; i++) {
            if(notifications.get(i).getTime() >= 5000) notifications.remove(i);
            drawNotification(notifications.get(i), i);
        }
    }


    public void addNotifications(String message, String title, Colour color){
        notifications.add(new Notifications(title, message, color, false));
    }

    public void addNotifications(String message, String title, boolean astolfo) {
        notifications.add(new Notifications(title, message, null, astolfo));
    }
}
