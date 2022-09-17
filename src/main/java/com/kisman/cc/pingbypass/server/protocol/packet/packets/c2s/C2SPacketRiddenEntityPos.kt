package com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s

import com.kisman.cc.pingbypass.server.protocol.ProtocolIds
import com.kisman.cc.pingbypass.server.protocol.packet.packets.C2SPacket
import com.kisman.cc.util.Globals.mc
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 23:04 of 21.08.2022
 */
class C2SPacketRiddenEntityPos(
    private var entityID : Int,
    private var x : Double,
    private var y : Double,
    private var z : Double
) : C2SPacket(
    ProtocolIds.C2S_RIDDEN_ENTITY
) {
    constructor() : this(
        -1,
        0.0,
        0.0,
        0.0
    )

    override fun readInnerBuffer(buffer: PacketBuffer) {
        entityID = buffer.readVarInt()
        x = buffer.readDouble()
        y = buffer.readDouble()
        z = buffer.readDouble()
    }

    override fun writeInnerBuffer(buffer: PacketBuffer) {
        buffer.writeVarInt(entityID)
        buffer.writeDouble(x)
        buffer.writeDouble(y)
        buffer.writeDouble(z)
    }

    override fun execute(manager: NetworkManager) {
        mc.addScheduledTask {
            if(mc.world != null) {
                mc.world.getEntityByID(entityID)?.setPosition(
                    x,
                    y,
                    z
                )
            }
        }
    }
}