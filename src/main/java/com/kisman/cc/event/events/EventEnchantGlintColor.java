package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

import java.awt.*;

public class EventEnchantGlintColor extends Event {

    private final Stage stage;

    private Color color;

    public EventEnchantGlintColor(Stage stage, Color color) {
        this.stage = stage;
        this.color = color;
    }

    public Stage getStage() {
        return stage;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public enum Stage {
        Item,
        Armor
    }
}
