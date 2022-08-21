package com.kisman.cc.pingbypass.server.protocol

import com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s.C2SPacketFriend
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 16:45 of 20.08.2022
 */
class ProtocolFactoryImpl : ProtocolFactory() {
    init {
        register(ProtocolIds.CS2_FRIEND, Supplier { C2SPacketFriend() })
    }
}