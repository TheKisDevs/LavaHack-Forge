package com.kisman.cc.util.minecraft;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.util.Random;

/**
 * @author Cubic
 * @since 14.08.2022
 */
public class Packets {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final NetHandlerPlayClient connection = mc.player.connection;

    private static final INetHandler handler = connection.getNetworkManager().getNetHandler();


    public static INetHandler getNetHandler(){
        return handler;
    }

    public static void processPacket(Packet packet){
        packet.processPacket(handler);
    }

    public static void send(Packet<?> packet){
        connection.sendPacket(packet);
    }

    public static void sendRandPacket(){
        Random random = new Random();
        byte[] bytes = new byte[random.nextInt(64)];
        for(int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) random.nextInt(256);
        PacketBuffer buffer = new PacketBuffer(Unpooled.copiedBuffer(bytes));
        CPacketCustomPayload packet = new CPacketCustomPayload("", buffer);
        connection.sendPacket(packet);
    }

    public static void send(byte[] bytes){
        connection.sendPacket(new CPacketCustomPayload("", new PacketBuffer(Unpooled.copiedBuffer(bytes))));
    }

    public static CPacketCustomPayload newCustomPacket(byte[] bytes){
        return new CPacketCustomPayload("", new PacketBuffer(Unpooled.copiedBuffer(bytes)));
    }

    public static PacketBuffer newBuf(byte[] bytes){
        return new PacketBuffer(Unpooled.copiedBuffer(bytes));
    }

    /**
     * @param bytes may never be used again after calling this method!
     */
    public static PacketBuffer newBufUnsafe(byte[] bytes){
        return new PacketBuffer(Unpooled.wrappedBuffer(bytes));
    }
}
