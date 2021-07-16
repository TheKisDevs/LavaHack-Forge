package com.kisman.cc.event;

import me.zero.alpine.type.Cancellable;
import net.minecraft.client.Minecraft;

public class Event extends Cancellable {
    private Era era;
    private float partialTicks;

    public Event() {
        this.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }

   public Event(Era era) {
       this.era = era;
       this.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
   }

    public Era getEra() {
        return era;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public enum Era {
        PRE,
        POST,
        PERI
    }
}
