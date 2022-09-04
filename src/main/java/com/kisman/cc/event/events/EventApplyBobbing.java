package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventApplyBobbing extends Event {

    private final float partialTicks;

    public EventApplyBobbing(float partialTicks){
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks(){
        return partialTicks;
    }
}
