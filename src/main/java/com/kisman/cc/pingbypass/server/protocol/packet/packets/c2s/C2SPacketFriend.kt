package com.kisman.cc.pingbypass.server.protocol.packet.packets.c2s

import com.kisman.cc.event.events.client.friend.FriendEvent
import com.kisman.cc.pingbypass.server.protocol.ProtocolIds
import com.kisman.cc.pingbypass.server.protocol.packet.packets.C2SPacket
import com.kisman.cc.util.manager.friend.FriendManager
import net.minecraft.network.NetworkManager
import net.minecraft.network.PacketBuffer

/**
 * @author _kisman_
 * @since 17:44 of 20.08.2022
 */
class C2SPacketFriend(
    private var event : FriendEvent?
) : C2SPacket(
    ProtocolIds.CS2_FRIEND
) {
    constructor() : this(null)

    override fun readInnerBuffer(
        buffer : PacketBuffer
    ) {
        try {
            event = FriendEvent(
                buffer.readString(Short.MAX_VALUE.toInt()),
                buffer.readEnumValue(FriendEvent.Type::class.java)
            )
        } catch(e : Exception) {
            LOGGER.error(e)
            throw e
        }
    }

    override fun writeInnerBuffer(
        buffer : PacketBuffer
    ) {
        try {
            buffer.writeString(event?.name!!)
            buffer.writeEnumValue(event?.type!!)
        } catch (e : Exception) {
            LOGGER.error(e)
            throw e
        }
    }

    override fun execute(
        manager : NetworkManager
    ) {
        if(event?.type == FriendEvent.Type.Add) {
            FriendManager.instance.addFriend(event?.name)
        } else if(event?.type == FriendEvent.Type.Remove) {
            FriendManager.instance.removeFriend(event?.name)
        }
    }
}