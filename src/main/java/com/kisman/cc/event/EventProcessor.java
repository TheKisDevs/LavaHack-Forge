package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventProcessor {
    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        Kisman.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        Kisman.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        Kisman.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Kisman.EVENT_BUS.post(event);
    }
}
