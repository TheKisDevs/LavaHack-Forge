package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.util.ResourceLocation

class EventCape(
        val info : NetworkPlayerInfo
) : Event() {
    var resLoc : ResourceLocation? = null
}