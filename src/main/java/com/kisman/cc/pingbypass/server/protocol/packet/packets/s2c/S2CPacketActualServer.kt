package com.kisman.cc.pingbypass.server.protocol.packet.packets.s2c

import com.kisman.cc.pingbypass.server.protocol.ProtocolIds
import com.kisman.cc.pingbypass.server.protocol.packet.packets.S2CPacket
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 12:25 of 21.08.2022
 */
class S2CPacketActualServer(
    private val ip : String?
) : S2CPacket(
    ProtocolIds.S2C_ACTUAL_IP
) {
    constructor() : this(null)

    override fun readInnerBuffer(buffer: PacketBuffer) {

    }

    override fun writeInnerBuffer(buffer: PacketBuffer) {

    }

    override fun execute(manager: NetworkManager) {

    }
}