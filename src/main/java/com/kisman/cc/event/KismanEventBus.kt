package com.kisman.cc.event

import me.zero.alpine.bus.EventManager

/**
 * @author _kisman_
 * @since 16:02 of 18.12.2022
 */
class KismanEventBus : EventManager() {
    override fun post(
        event
        : Any
    ) {
        super.post(event)

        if(event is Event && event.mirrorEvent != null) {
            post(event.mirrorEvent)
        }
    }
}