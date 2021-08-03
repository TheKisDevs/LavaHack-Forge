package com.kisman.cc.notification;

public class Notification {
    public static int offsetY;

    public static boolean moduleEnabled = false;

    public static int getOffsetY() {
        return offsetY;
    }

    public static void setOffsetY(int offsetY) {
        Notification.offsetY = offsetY;
    }

    public static boolean isModuleEnabled() {
        return moduleEnabled;
    }

    public static void setModuleEnabled(boolean moduleEnabled) {
        Notification.moduleEnabled = moduleEnabled;
    }
}
