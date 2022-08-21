package com.kisman.cc.pingbypass.server.services

import com.kisman.cc.event.Event
import com.kisman.cc.event.events.EventPlayerMotionUpdate
import com.kisman.cc.features.module.client.PingBypass
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.enums.PingBypassProtocol
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketInput

/**
 * @author _kisman_
 * @since 22:37 of 21.08.2022
 */
object CPacketInputService {
    init {

    }

    private val riding = Listener<EventPlayerMotionUpdate.Riding>(EventHook {
        if(
            it.era == Event.Era.PRE
            && PingBypass.isToggled
            && PingBypass.protocol.valEnum == PingBypassProtocol.New
        ) {
            mc.player.connection.sendPacket(CPacketInput(
                it.moveStrafing,
                it.moveForward,
                it.isJump,
                it.isSneak
            ))

            val entity = mc.player.lowestRidingEntity

            if(entity != mc.player && entity.canPassengerSteer()) {
                //TODO: mc.player.connection.sendPacket()
            }
        }
    })
}