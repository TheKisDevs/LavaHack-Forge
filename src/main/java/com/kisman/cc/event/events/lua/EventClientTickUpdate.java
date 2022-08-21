package com.kisman.cc.event.events.lua;

import com.kisman.cc.event.Event;

import static com.kisman.cc.util.Globals.mc;

public class EventClientTickUpdate extends Event {
    public boolean isPingBypass = false;

    public EventClientTickUpdate() {
        this(false);
    }

    public EventClientTickUpdate(
            boolean isPingBypass
    ) {
        this.isPingBypass = isPingBypass;
    }

    public String getName() {
        return "tick";
    }

    public boolean safe() {
        return mc.player != null && mc.world != null;
    }
}
