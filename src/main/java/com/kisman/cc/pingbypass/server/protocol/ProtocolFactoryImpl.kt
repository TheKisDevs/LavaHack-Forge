package com.kisman.cc.pingbypass.server.protocol

import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketActualPos
import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketBindFeature
import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketFriend
import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketRiddenEntityPos
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 16:45 of 20.08.2022
 */
class ProtocolFactoryImpl : ProtocolFactory() {
    init {
        register(ProtocolIds.CS2_FRIEND, Supplier { C2SPacketFriend() })
        register(ProtocolIds.C2S_BIND_FEATURE, Supplier { C2SPacketBindFeature() })
        register(ProtocolIds.C2S_RIDDEN_ENTITY, Supplier { C2SPacketRiddenEntityPos() })
        register(ProtocolIds.CS2_ACTUAL_POS, Supplier { C2SPacketActualPos() })
    }
}