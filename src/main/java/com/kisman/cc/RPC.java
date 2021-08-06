package com.kisman.cc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class RPC {

    public static boolean isHP = true;

    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public static void startRPC() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));

        String discordID = "872062302755635201";
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);

        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        if(Minecraft.getMinecraft().isSingleplayer()) {
            discordRichPresence.details = "Playing singleplayer";
        } else {
            discordRichPresence.details = "Playing multiplayer!";
        }
        if(isIsHP()) {
            discordRichPresence.state = "HP: " + (int) Minecraft.getMinecraft().player.getHealth();
        } else {
            if(!Minecraft.getMinecraft().isSingleplayer()) {
                discordRichPresence.state = "IP: " + Minecraft.getMinecraft().world.getMinecraftServer().getServerHostname();
            } else {
                discordRichPresence.state = "IP: singleplayer";
            }
        }
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }

    public static boolean isIsHP() {
        return isHP;
    }

    public static void setIsHP(boolean isHP) {
        RPC.isHP = isHP;
    }
}
