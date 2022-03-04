package com.kisman.cc.oldclickgui.notification;

import java.awt.*;

public class Notifications {
    private final String title;
    private final String message;
    private final Color color;
    private final long start;

    public Notifications(String title, String message, Color color){
        this.title = title;
        this.message = message;
        this.color = color;
        start = System.currentTimeMillis();

    }

    public String getTitle(){
        return this.title;
    }

    public String getMessage(){
        return this.message;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    public Color getColor() {
        return color;
    }
}
