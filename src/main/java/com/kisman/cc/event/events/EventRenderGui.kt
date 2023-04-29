package com.kisman.cc.event.events

import com.kisman.cc.event.Event

/**
 * @author _kisman_
 * @since 13:47 of 23.04.2023
 */
open class EventRenderGui : Event() {
    class Pre : EventRenderGui()
    class Post : EventRenderGui()
}