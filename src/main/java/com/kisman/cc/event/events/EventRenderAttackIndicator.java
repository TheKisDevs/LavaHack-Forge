package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderAttackIndicator extends Event {

    private final float partialTicks;

    private final ScaledResolution scaledResolution;

    public EventRenderAttackIndicator(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
