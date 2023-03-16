package com.kisman.cc.features.rpc;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.DiscordRPCModule;
import com.kisman.cc.features.rpc.library.DiscordEventHandlers;
import com.kisman.cc.features.rpc.library.DiscordRPC;
import com.kisman.cc.features.rpc.library.DiscordRichPresence;
import com.kisman.cc.util.Globals;
import com.kisman.cc.util.enums.RPCImages;

public class RPC implements Globals {
    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static Thread thread;
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public static synchronized void startRPC() {
        if (thread != null) {
            thread.interrupt();
        }
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));

        String discordID = "895232773961445448";
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);

        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;

        discordRichPresence.largeImageKey = "logo";
        discordRichPresence.largeImageText = "join discord now: https://discord.gg/NNn7WXfkNB";

        discordRichPresence.smallImageKey = "plus";
        discordRichPresence.smallImageText = Kisman.NAME;

        discordRichPresence.details = Kisman.NAME + " | " + Kisman.VERSION;

        discordRichPresence.partyId = "5657657-351d-4a4f-ad32-2b9b01c91657";
        discordRichPresence.partySize = 1;
        discordRichPresence.partyMax = 10;
        discordRichPresence.joinSecret = "join";
        discordRPC.Discord_UpdatePresence(discordRichPresence);
        new Thread(() -> {
            if(DiscordRPCModule.instance.impr.getValBoolean()) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        discordRichPresence.largeImageKey = ((RPCImages) DiscordRPCModule.instance.imgMode.getValEnum()).getKey();
                        String details, state;
                        discordRPC.Discord_RunCallbacks();
                        if (mc.isIntegratedServerRunning() || mc.world == null) details = Kisman.getVersion();
                        else details = Kisman.getVersion() + " - Playing Multiplayer";
                        state = "";
                        if (mc.isIntegratedServerRunning()) state = Kisman.getName() + " on tope!";
                        else if (mc.getCurrentServerData() != null) if (!mc.getCurrentServerData().serverIP.equals("")) state = "Playing on " + mc.getCurrentServerData().serverIP;
                        else state = "Main Menu";
                        discordRichPresence.details = details;
                        discordRichPresence.state = state;
                        discordRPC.Discord_UpdatePresence(discordRichPresence);
                    } catch (Exception e2) {e2.printStackTrace();}
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e3) {e3.printStackTrace();}
                }
            }
        }, "Discord-RPC-Callback-Handler").start();
    }

    public static void stopRPC() {
        if (thread != null && !thread.isInterrupted())
        {
            thread.interrupt();
            thread = null;
        }
        discordRPC.Discord_Shutdown();
    }
}
