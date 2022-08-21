package com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s

import com.kisman.cc.features.module.client.PingBypass
import com.kisman.cc.pingbypass.server.PingBypassServer
import com.kisman.cc.pingbypass.server.protocol.ProtocolIds
import com.kisman.cc.pingbypass.server.protocol.packet.packets.C2SPacket
import com.kisman.cc.util.Globals.mc
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.CPacketPlayer

/**
 * @author _kisman_
 * @since 14:26 of 21.08.2022
 */
class C2SPacketActualPos(
    private var x : Double,
    private var y : Double,
    private var z : Double
) : C2SPacket(
    ProtocolIds.CS2_ACTUAL_POS
) {
    private var packetfly = false

    override fun readInnerBuffer(
        buffer : PacketBuffer
    ) {
        x = buffer.readDouble()
        y = buffer.readDouble()
        z = buffer.readDouble()
        packetfly = buffer.readBoolean()
    }

    override fun writeInnerBuffer(
        buffer : PacketBuffer
    ) {
        buffer.writeDouble(x)
        buffer.writeDouble(y)
        buffer.writeDouble(z)
        buffer.writeBoolean(packetfly)
    }

    override fun execute(
        manager : NetworkManager
    ) {
        mc.addScheduledTask {
            PingBypassServer.actualPos = this
            PingBypassServer.packetflying = packetfly
        }
    }

    fun valid(
        packet : CPacketPlayer
    ) : Boolean {
        val pX = packet.getX(x) - x
        val pY = packet.getX(y) - y
        val pZ = packet.getX(z) - z

        return pX * pX + pY * pY + pZ * pZ <= PingBypass.positionRange.valDouble
    }
}