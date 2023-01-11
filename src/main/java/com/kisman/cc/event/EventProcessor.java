package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventResolutionUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.event.events.lua.EventClientChat;
import com.kisman.cc.event.events.lua.EventClientTickUpdate;
import com.kisman.cc.event.events.lua.EventRender2D;
import com.kisman.cc.event.events.lua.EventRender3D;
import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.CustomMainMenuModule;
import com.kisman.cc.features.module.client.custommainmenu.CustomMainMenu;
import com.kisman.cc.features.module.combat.AutoRer;
import com.kisman.cc.util.TickRateUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class EventProcessor {
    private final Minecraft mc = Minecraft.getMinecraft();

    public AtomicBoolean ongoing;

    public int oldWidth = -1, oldHeight = -1;

    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(totempop);
        Kisman.EVENT_BUS.subscribe(TickRateUtil.INSTANCE.listener);
        Kisman.EVENT_BUS.subscribe(packet);
        Kisman.instance.progressBar.steps++;

        ongoing = new AtomicBoolean(false);
    }

    public void init() {
        Kisman.EVENT_BUS.post(new EventProgressBar("Event Processor"));
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        try {
            if (AutoRer.instance.lagProtect.getValBoolean()) disableCa();
            AutoRer.instance.placePos.setBlockPos(null);
            Kisman.instance.configManager.getSaver().init();
        } catch(Exception ignored) {}
    }

    private void disableCa() {
        AutoRer.instance.setToggled(false);
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(AutoRer.instance.lagProtect.getValBoolean()) disableCa();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Kisman.instance.scriptManager.runCallback("tick");
        Kisman.EVENT_BUS.post(new EventClientTickUpdate());
        if(oldWidth != mc.displayWidth || oldHeight != mc.displayHeight) {
            oldWidth = mc.displayWidth;
            oldHeight = mc.displayHeight;
            new EventResolutionUpdate(oldWidth, oldHeight).post();
        }
        if(CustomMainMenuModule.instance != null) CustomMainMenu.update();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ClientChatEvent event) {
        if(event.getMessage().startsWith(Kisman.instance.commandManager.prefixString)) {
            try {
                Kisman.instance.commandManager.runCommands(event.getMessage().substring(0));
                event.setCanceled(true);
            } catch (Exception ignored) {}
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packet = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketRespawn && AutoRer.instance.lagProtect.getValBoolean()) disableCa();
        if(event.getPacket() instanceof SPacketChat && Config.instance.configurate.getValBoolean()) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            String message = packet.chatComponent.getUnformattedText();
            if(message.contains("+")) {
                String formattedMessage = message.substring(message.indexOf("+"));
                try {
                    String[] args = formattedMessage.split(" ");
                    if (args[0] != null && args[1] != null) {
                        Module module = Kisman.instance.moduleManager.getModule(args[1]);
                        if (module == null) return;
                        if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("+disable")) module.setToggled(false);
                        else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("+enable")) module.setToggled(true);
                        else if (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("+block")) module.block = true;
                        else if (args[0].equalsIgnoreCase("unblock") || args[0].equalsIgnoreCase("+unlock")) module.block = true;
                    }
                } catch(Exception ignored) {}
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> totempop = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            TotemPopEvent totemPopEvent = new TotemPopEvent(((SPacketEntityStatus) event.getPacket()).getEntity(mc.world));
            MinecraftForge.EVENT_BUS.post(totemPopEvent);
            if(totemPopEvent.isCanceled()) event.cancel();
        }
    });

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        Kisman.instance.scriptManager.runCallback("hud");
        Kisman.EVENT_BUS.post(new EventRender2D(event.getPartialTicks()));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Kisman.EVENT_BUS.post(new EventRender3D(event.getPartialTicks()));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onClientChat(ClientChatEvent event) {
        EventClientChat eventClientChat = new EventClientChat(event.getMessage());
        Kisman.EVENT_BUS.post(eventClientChat);
        if(eventClientChat.cancelled) event.setMessage(eventClientChat.message);
    }
}