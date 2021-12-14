package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D extends Event {
    private final ScaledResolution resolution;
    private final float partialticks;

    public EventRender2D(ScaledResolution resolution, float partialticks) {
        this.resolution = resolution;
        this.partialticks = partialticks;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

    public float getPartialTicks() {
        return this.partialticks;
    }
}
