package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.util.chat.cubic.ChatUtility
import io.netty.buffer.ByteBuf
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer

/**
 * @author _kisman_
 * @since 14:19 of 05.06.2022
 */
class PacketIDGetterTest : Module(
    "PacketIDGetterTest",
    "Test for rdupe command.",
    Category.DEBUG
) {
    init {
        instance = this
    }

    companion object {
        var instance : PacketIDGetterTest? = null
    }

    fun packetReceived(
        direction : EnumPacketDirection,
        id : Int,
        packet : Packet<*>,
        buff : ByteBuf?
    ) : Packet<*>? {
        if(packet is CPacketPlayer.Position) {
            ChatUtility.info().printClientModuleMessage("CPacketPlayer.Position id1($id), id2(${EnumConnectionState.getById(0).getPacketId(direction, packet)}), id3(${EnumConnectionState.getFromPacket(packet).id})")
            toggled = false
        }
        return packet
    }
}