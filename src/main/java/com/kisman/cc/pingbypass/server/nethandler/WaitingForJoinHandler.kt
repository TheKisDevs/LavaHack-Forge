package com.kisman.cc.pingbypass.server.nethandler

import com.kisman.cc.pingbypass.server.protocol.ProtocolFactoryImpl
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraft.network.play.client.CPacketCustomPayload
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketKeepAlive

/**
 * @author _kisman_
 * @since 13:29 of 21.08.2022
 */
class WaitingForJoinHandler(
    manager : NetworkManager
) : BaseNetHandler(
    manager,
    100_000
),
    IPingBypassNetHandler
{
    private val factory = ProtocolFactoryImpl()

    override fun processCustomPayload(
        packet : CPacketCustomPayload
    ) {
        if("PingBypass" == packet.channelName) {
            factory.handle(
                packet.bufferData,
                manager
            )
        }
    }

    override fun processChatMessage(
        packet : CPacketChatMessage
    ) {}

    override fun processKeepAlive(
        packet : CPacketKeepAlive
    ) {
        timer.reset()
        manager.sendPacket(SPacketKeepAlive(0))
    }
}