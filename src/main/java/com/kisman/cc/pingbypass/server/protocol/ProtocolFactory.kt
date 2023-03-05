package com.kisman.cc.pingbypass.server.protocol

import com.kisman.cc.pingbypass.server.protocol.packet.PingBypassPacket
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 16:22 of 20.08.2022
 */
open class ProtocolFactory {
    private val factories = ConcurrentHashMap<Int, Supplier<PingBypassPacket<*>>>()

    fun register(
        id : Int,
        packet : Supplier<PingBypassPacket<*>>
    ) {
        factories[id] = packet
    }

    fun handle(
        buf : PacketBuffer,
        manager : NetworkManager
    ) {
        try {
            convert(buf).execute(manager)
        } catch(e : Exception) {
            throw RuntimeException(e)
        }
    }

    fun convert(
        buf : PacketBuffer
    ) : PingBypassPacket<*> {
        val id = buf.readVarInt()

        val factory = factories[id]

        if(factory == null) {
            LOGGER.error("Could not find Packet Factory for id $id")
            throw Exception("Could not find Packet Factory for id $id")
        }

        val packet = factory.get()

        try {
            packet.readInnerBuffer(buf)
        } catch(e : Exception) {
            e.printStackTrace()
            throw e
        }

        return packet
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }
}