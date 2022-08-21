package com.kisman.cc.pingbypass.server

import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketActualPos
import net.minecraft.network.NetworkManager

/**
 * @author _kisman_
 * @since 15:45 of 20.08.2022
 */
object PingBypassServer {
    @Volatile var stay = false
    @Volatile var connected = false
    @Volatile var manager : NetworkManager? = null

    var actualPos : C2SPacketActualPos? = null
    var packetflying = false

    var server = false

    fun connected() : Boolean = server && connected
}