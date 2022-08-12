package com.kisman.cc.event.events

import com.kisman.cc.event.Event

class TurnEvent(
    @JvmField var yaw : Float,
    @JvmField var pitch : Float,
    @JvmField var rotationYaw : Float,
    @JvmField var rotationPitch : Float,
    @JvmField var prevYaw : Float,
    @JvmField var prevPitch : Float
) : Event()