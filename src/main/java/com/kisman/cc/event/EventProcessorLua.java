package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventProcessorLua {
    public EventProcessorLua() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Kisman.instance.scriptManager.runCallback("tick");
    }
}
