package com.kisman.cc.mixin.accessors

import net.minecraft.network.Packet

/**
 * @author _kisman_
 * @since 21:58 of 10.01.2023
 */
interface INetworkManager {
    fun sendPacketNoEvent(
        packet : Packet<*>?,
        post : Boolean
    )

    fun sendPacketNoEvent(
        packet : Packet<*>?
    )
}