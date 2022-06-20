package com.kisman.cc.util;

import com.kisman.cc.mixin.mixins.accessor.INetworkManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;

public class NetworkUtil implements Globals {

    public static void send(Packet<?> packet)
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            connection.sendPacket(packet);
        }
    }
    @SuppressWarnings("UnusedReturnValue")
    public static Packet<?> sendPacketNoEvent(Packet<?> packet)
    {
        return sendPacketNoEvent(packet, true);
    }

    public static Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post)
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null)
        {
            INetworkManager manager =
                    (INetworkManager) connection.getNetworkManager();

            return manager.sendPacketNoEvent(packet, post);
        }

        return null;
    }
}
