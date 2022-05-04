package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.network.PacketBuffer

class EventC00Handshake(
        val buffer : PacketBuffer
) : Event()