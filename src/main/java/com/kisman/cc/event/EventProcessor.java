package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;

import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventProcessor {
    private Minecraft mc = Minecraft.getMinecraft();

    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(totempop);
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
/*        if(Keyboard.getEventKey() <= 0) return;

        for(Command cmd : Kisman.instance.commandManager.commands) {
            if(cmd.getKey() == Keyboard.getEventKey()) {
                cmd.runCommand(".", cmd.getExecute());
            }
        }*/
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
/*        Kisman.instance.moduleManager.onTick(event);
        for(EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
            if(player == null || player.getHealth() > 0) {
                continue;
            }
        }*/
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> totempop = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            TotemPopEvent totemPopEvent = new TotemPopEvent(((SPacketEntityStatus) event.getPacket()).getEntity(mc.world));
            MinecraftForge.EVENT_BUS.post(totemPopEvent);

            if(totemPopEvent.isCanceled()) {
                event.cancel();
            }
        }
    });
}