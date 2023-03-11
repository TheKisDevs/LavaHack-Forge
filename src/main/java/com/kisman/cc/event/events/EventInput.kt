package com.kisman.cc.event.events

import com.kisman.cc.event.Event

/**
 * @author _kisman_
 * @since 18:20 of 08.03.2023
 */
open class EventInput : Event() {
    class Keyboard : EventInput()
    class Mouse : EventInput()
}