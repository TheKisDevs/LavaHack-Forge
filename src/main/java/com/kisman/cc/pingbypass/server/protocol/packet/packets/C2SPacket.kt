package com.kisman.cc.pingbypass.server.protocol.packet.packets

import com.kisman.cc.pingbypass.server.protocol.packet.IPacket
import com.kisman.cc.pingbypass.server.protocol.packet.PingBypassPacket
import io.netty.buffer.Unpooled
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketCustomPayload
import org.apache.logging.log4j.LogManager

/**
 * @author _kisman_
 * @since 16:53 of 20.08.2022
 */
abstract class C2SPacket(
    private val id : Int
) : CPacketCustomPayload(
    "PingBypass",
    PacketBuffer(Unpooled.buffer())
),
    PingBypassPacket<INetHandlerPlayServer>,
    IPacket
{

    override fun getId() : Int = getState().getPacketId(EnumPacketDirection.SERVERBOUND, CPCP)

    override fun writePacketData(
        buf : PacketBuffer
    ) {
        synchronized(bufferData) {
            bufferData.writeVarInt(id)
            writeInnerBuffer(bufferData)
            super.writePacketData(buf)
        }
    }

    companion object {
        @JvmStatic
        protected val LOGGER = LogManager.getLogger()
        private val CPCP = CPacketCustomPayload()
    }
}