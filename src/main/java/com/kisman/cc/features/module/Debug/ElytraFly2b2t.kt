package com.kisman.cc.features.module.Debug

import com.kisman.cc.event.events.EventPlayerTravel
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module

/**
 * @author _kisman_
 * @since 14:13 of 02.11.2022
 */
class ElytraFly2b2t : Module(
    "ElytraFly2b2t",
    "skidded from bloom ware",
    Category.DEBUG
) {
    /*private enum class Modes {
        Packet {
            override fun handle(
                event : EventPlayerTravel
            ) {

            }
        },
        Control {
            override fun handle(
                event : EventPlayerTravel
            ) {

            }
        },
        Boost {
            override fun handle(
                event : EventPlayerTravel
            ) {
                if(mc.player.wasFallFlying && mc.gameSettings.keyBindJump.isPressed) {
                    val motion = getMotionOnKey(hspeed / 72.0, vspeed 72.0)

                    mc.player.addVelocity(0.0, hoverspeed / 72.0, 0.0)
                    mc.player.capabilities.isFlying = false
                    event.cancel()

                }
            }
        }

        ;
        
        abstract fun handle(event : EventPlayerTravel)
    }*/
}