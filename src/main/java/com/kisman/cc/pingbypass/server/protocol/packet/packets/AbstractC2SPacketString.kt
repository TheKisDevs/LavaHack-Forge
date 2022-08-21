package com.kisman.cc.pingbypass.server.protocol.packet.packets

import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 17:55 of 20.08.2022
 */
abstract class AbstractC2SPacketString(
    id : Int,
    private var string : String
) : C2SPacket(
    id
) {
    constructor(
        id : Int
    ) : this(
        id,
        ""
    )

    override fun readInnerBuffer(
        buffer : PacketBuffer
    ) {
        string = buffer.readString(Short.MAX_VALUE.toInt())
    }

    override fun writeInnerBuffer(
        buffer : PacketBuffer
    ) {
        buffer.writeString(string)
    }

    override fun toString() : String = string
}