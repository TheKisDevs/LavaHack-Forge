package com.kisman.cc.features.module.player.packetcanceller

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import io.netty.buffer.ByteBuf
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.Packet

/**
 * TODO
 *
 * @author _kisman_
 * @since 14:13 of 04.06.2022
 */
class PacketCancellerHandler {
    companion object {
        private val toCancelCPackets = HashSet<Int>()
        private val toCancelSPackets = HashSet<Int>()

        private val exceptions : HashSet<Packet<*>> = HashSet()

        val state = false

        private fun getSet(direction: EnumPacketDirection) : HashSet<Int>? {
//            return if(direction == EnumPacketDirection.CLIENTBOUND)
            return null
        }

        fun packetReceived(
            direction : EnumPacketDirection,
            id : Int,
            packet : Packet<*>,
            buff : ByteBuf?
        ) : Packet<*>? {
            if(state) {
                if(exceptions.remove(packet)) {
                    return packet
                }
                return null
            }
            return packet
        }

        private val send = Listener<PacketEvent.Send>(EventHook {
//            if(it.packet.)
        })

        private val receive = Listener<PacketEvent.Receive>(EventHook {

        })

        init {
            Kisman.EVENT_BUS.subscribe(send)
        }
    }
}