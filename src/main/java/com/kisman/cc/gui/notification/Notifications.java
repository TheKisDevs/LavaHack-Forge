package com.kisman.cc.gui.notification;

import com.kisman.cc.util.Colour;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.awt.*;

public class Notifications {
    private final String title;
    private final String message;
    private final Colour color;
    private final long start;
    public boolean astolfo;

    public Notifications(String title, String message, Colour color, boolean astolfo){
        this.title = title;
        this.message = message;
        this.color = color;
        start = System.currentTimeMillis();
        this.astolfo = astolfo;
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

    public Colour getColor() {
        return color != null ? color : new Colour(ColorUtils.astolfoColors(100, 100));
    }
}
