package com.kisman.cc.event.events

import com.kisman.cc.event.Event

class EventC00Handshake(
        var ip : String,
        val defaultIp : String
) : Event()