package com.kisman.cc.event;

import me.zero.alpine.event.type.Cancellable;
import net.minecraft.client.Minecraft;

public class Event extends Cancellable {
    private Era era;
    private float particalTicks;

    public Event() {
        this.particalTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public Event(Era era) {
        this.era = era;
        this.particalTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public Era getEra() {
        return era;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public float getParticalTicks() {
        return particalTicks;
    }

    public void setParticalTicks(float particalTicks) {
        this.particalTicks = particalTicks;
    }

    public enum Era {
        PRE,
        POST,
        PERI
    }
}
