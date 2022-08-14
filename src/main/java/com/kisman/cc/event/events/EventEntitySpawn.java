package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.Entity;

public class EventEntitySpawn extends Event {

    private final Entity entity;

    public EventEntitySpawn(Entity entity){
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
