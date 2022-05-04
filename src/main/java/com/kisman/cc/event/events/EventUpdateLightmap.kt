package com.kisman.cc.event.events

import com.kisman.cc.event.Event

open class EventUpdateLightmap : Event() {
    class Pre : EventUpdateLightmap()

    class Post(
            var lightmapColors : IntArray
    ) : EventUpdateLightmap()
}