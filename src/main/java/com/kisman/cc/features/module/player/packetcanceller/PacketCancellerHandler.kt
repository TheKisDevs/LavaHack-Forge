package com.kisman.cc.features.module.player.packetcanceller

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

/**
 * TODO
 *
 * @author _kisman_
 * @since 14:13 of 04.06.2022
 */
class PacketCancellerHandler {
    companion object {
        private val toCancelCPackets = emptySet<Int>()
        private val toCancelSPackets = emptySet<Int>()

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