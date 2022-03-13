package com.kisman.cc;

import club.minnced.discord.rpc.*;

public class RPC {
    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public static void startRPC() {
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
    }

    public static void updateRPC() {
        discordRichPresence.details = Kisman.getName() + " | " + Kisman.getVersion();
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }
}
