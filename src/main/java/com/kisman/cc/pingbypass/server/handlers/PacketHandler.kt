package com.kisman.cc.pingbypass.server.handlers

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent.Receive
import com.kisman.cc.event.events.PacketEvent.Send
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.Packet
import net.minecraftforge.common.MinecraftForge

/**
 * @author _kisman_
 * @since 18:14 of 20.08.2022
 */
class PacketHandler {
    private val send = Listener<Send>(EventHook {

    })

    private val receive = Listener<Receive>(EventHook {
        sendPacketToClient(it.packet)
    })

    init {
        MinecraftForge.EVENT_BUS.register(this)
        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(receive)
    }

    fun sendPacketToClient(
        packet : Packet<*>
    ) {
        //TODO
    }

    fun sendPacketToServer(
        packet : Packet<*>
    ) {
        //TODO
    }
}