@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.util.minecraft

import com.kisman.cc.mixin.accessors.INetworkManager
import com.kisman.cc.util.Globals.mc
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.util.Attribute
import io.netty.util.AttributeKey
import io.netty.util.concurrent.EventExecutor
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import java.net.SocketAddress

/**
 * @author _kisman_
 * @since 22:28 of 10.01.2023
 */

fun sendPacketNoEvent(
    packet : Packet<*>
) {
    sendPacketNoEvent(packet, true)
}

fun sendPacketNoEvent(
    packet : Packet<*>,
    post : Boolean
) {
    if(mc.player != null) {
        (mc.player.connection.networkManager as INetworkManager).sendPacketNoEvent(packet, post)
    }
}

fun position(
    x : Double,
    y : Double,
    z : Double,
    ground : Boolean
) : CPacketPlayer.Position = CPacketPlayer.Position(
    x,
    y,
    z,
    ground
)

fun rotation(
    yaw : Float,
    pitch : Float,
    ground : Boolean
) : CPacketPlayer.Rotation = CPacketPlayer.Rotation(
    yaw,
    pitch,
    ground
)

fun positionRotation(
    x : Double,
    y : Double,
    z : Double,
    yaw : Float,
    pitch : Float,
    ground : Boolean
) : CPacketPlayer.PositionRotation = CPacketPlayer.PositionRotation(
    x,
    y,
    z,
    yaw,
    pitch,
    ground
)

fun receive(
    packet : Packet<*>
) {
    (mc.player.connection.networkManager as INetworkManager).channelRead00(null, packet)
}

fun <T : INetHandler> processPacket(
    packet : Packet<T>
) {
    packet.processPacket(mc.player.connection.networkManager.netHandler as T)
}