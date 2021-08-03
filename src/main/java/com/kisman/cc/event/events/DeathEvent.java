package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent extends Event {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        super();
        this.player = player;
    }
}
