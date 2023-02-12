package com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s

import com.kisman.cc.Kisman
import com.kisman.cc.features.Binder
import com.kisman.cc.util.enums.BindType
import com.kisman.cc.pingbypass.server.protocol.ProtocolIds
import com.kisman.cc.pingbypass.server.protocol.packet.packets.C2SPacket
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 0:06 of 17.09.2022
 */
class C2SPacketBindFeature(
    var name : String
) : C2SPacket(
    ProtocolIds.C2S_BIND_FEATURE
) {
    constructor() : this("name")

    private val binder = Binder(
        name,
        BindType.Keyboard,
        -1,
        -1,
        false
    )

    override fun readInnerBuffer(
        buffer : PacketBuffer
    ) {
        try {
            binder.setKeyboardKey(buffer.readInt())
            binder.setMouseButton(buffer.readInt())
            binder.setType(buffer.readEnumValue(BindType::class.java));
            binder.setHold(buffer.readBoolean())
        } catch(e : Exception) {
            LOGGER.error(e)
            throw e
        }
    }

    override fun writeInnerBuffer(
        buffer : PacketBuffer
    ) {
        try {
            buffer.writeInt(binder.getKeyboardKey())
            buffer.writeInt(binder.getMouseButton())
            buffer.writeEnumValue(binder.getType())
            buffer.writeBoolean(binder.isHold())
        } catch(e : Exception) {
            LOGGER.error(e)
            throw e
        }
    }

    override fun execute(
        manager : NetworkManager
    ) {
        val module = Kisman.instance.moduleManager.getModule(name)

        if(module != null) {
            module.setKeyboardKey(binder.getKeyboardKey())
            module.setMouseButton(binder.getMouseButton())
            module.setType(binder.getType())
            module.setHold(binder.isHold())
        }
    }
}