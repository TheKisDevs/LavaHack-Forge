package com.kisman.cc.util.minecraft

import com.kisman.cc.mixin.accessors.INetworkManager
import com.kisman.cc.util.Globals.mc
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer

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