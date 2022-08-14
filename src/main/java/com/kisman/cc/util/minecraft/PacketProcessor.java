package com.kisman.cc.util.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class PacketProcessor {

    private static final INetHandler handler = Minecraft.getMinecraft().player.connection.getNetworkManager().getNetHandler();

    public static INetHandler getNetHandler(){
        return handler;
    }

    public static void processPacket(Packet packet){
        packet.processPacket(handler);
    }
}
