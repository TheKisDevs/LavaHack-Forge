package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.network.Packet;

public interface INetworkManager {

    Packet<?> sendPacketNoEvent(Packet<?> packetIn);

    Packet<?> sendPacketNoEvent(Packet<?> packetIn, boolean post);

}
