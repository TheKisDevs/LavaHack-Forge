package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventStopUsingItem extends Event {

    private final EntityPlayer player;

    public EventStopUsingItem(EntityPlayer player){
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
