package com.kisman.cc.pingbypass.server.protocol.packet

import net.minecraft.network.EnumConnectionState

/**
 * @author _kisman_
 * @since 17:15 of 20.08.2022
 */
interface IPacket {
    fun getId() : Int

    fun getState() : EnumConnectionState = EnumConnectionState.PLAY
}