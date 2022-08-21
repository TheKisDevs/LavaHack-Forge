package com.kisman.cc.pingbypass.server.protocol.packet

import net.minecraft.network.INetHandler
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 17:07 of 20.08.2022
 */
interface PingBypassPacket<T : INetHandler> : Packet<T> {
    @Throws(Exception::class) fun readInnerBuffer(buffer : PacketBuffer)
    @Throws(Exception::class) fun writeInnerBuffer(buffer : PacketBuffer)
    @Throws(Exception::class) fun execute(manager : NetworkManager)
}