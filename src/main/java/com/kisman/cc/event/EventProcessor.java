package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.commands.Flip;
import com.kisman.cc.event.events.*;

import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.file.SaveConfig;
import com.kisman.cc.hypixel.util.ConfigHandler;
import com.kisman.cc.module.combat.AutoTrap;
import com.kisman.cc.util.TickRateUtil;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;

public class EventProcessor {
    private Minecraft mc = Minecraft.getMinecraft();

    //NEC vars
    public boolean hasRan = false;

    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(totempop);
        Kisman.EVENT_BUS.subscribe(TickRateUtil.INSTANCE.listener);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AutoTrap.instance.setToggled(false);
        SaveConfig.init();
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ClientChatEvent event) {
        if(event.getMessage().startsWith(Kisman.instance.commandManager.cmdPrefixStr)) {
            try {
                Kisman.instance.commandManager.runCommands(event.getMessage().substring(0));
                event.setCanceled(true);
            } catch (Exception e) {}
        }
    }

    //NEC events
    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
            Timer timer = new Timer();
            hasRan = true;
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Flip.flip(mc.player);
                        }
                    },
                    2000
            );
        } else {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                    "Flip",
                    "true"
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chat(ClientChatReceivedEvent event) {
        if (!event.getMessage().getUnformattedText().startsWith("Your new API key is ")) return;
        String key = event.getMessage().getUnformattedText().split("key is ")[1];
        ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", key);
        ChatUtils.complete(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" + TextFormatting.GRAY + " API Key set to " + TextFormatting.GREEN + key
        );
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> totempop = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            TotemPopEvent totemPopEvent = new TotemPopEvent(((SPacketEntityStatus) event.getPacket()).getEntity(mc.world));
            MinecraftForge.EVENT_BUS.post(totemPopEvent);
            if(totemPopEvent.isCanceled()) event.cancel();
        }
    });
}