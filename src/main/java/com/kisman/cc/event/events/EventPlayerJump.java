package com.kisman.cc.event.events;
import com.kisman.cc.event.Event;
import net.minecraft.entity.Entity;

public class EventPlayerJump extends Event {
    public Entity entity;

    public EventPlayerJump(Entity entity) {
        this.entity = entity;
    }
}