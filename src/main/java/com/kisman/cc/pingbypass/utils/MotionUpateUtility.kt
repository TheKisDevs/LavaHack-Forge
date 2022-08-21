package com.kisman.cc.pingbypass.utils

import com.kisman.cc.Kisman
import com.kisman.cc.event.Event
import com.kisman.cc.event.events.EventPlayerMotionUpdate
import com.kisman.cc.event.events.lua.EventClientTickUpdate
import com.kisman.cc.util.Globals.mc

/**
 * @author _kisman_
 * @since 14:53 of 21.08.2022
 */

fun makeMotionUpdate(
    x : Double,
    y : Double,
    z : Double,
    yaw : Float,
    pitch : Float,
    ground : Boolean,
    spoofRotation : Boolean
) {
    Kisman.EVENT_BUS.post(EventClientTickUpdate())

    if(mc.player.isRiding) {
        /*TODO: val riding = EventPlayerMotionUpdate.Riding(
            Event.Era.PRE,
            yaw,
            pitch,
            z,
            y,
            z,
            ground,

        )*/
    }
}