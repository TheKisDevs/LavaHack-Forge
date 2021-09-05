package com.kisman.cc.event;

import com.kisman.cc.Kisman;
// import com.kisman.cc.event.events.LivingDeathEvent;

import com.kisman.cc.command.Command;
import me.zero.alpine.listener.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class EventProcessor {
    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
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
        Kisman.instance.moduleManager.onTick(event);
        for(EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
            if(player == null || player.getHealth() > 0) {
                continue;
            }
        }
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


}