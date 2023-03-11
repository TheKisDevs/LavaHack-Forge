package com.kisman.cc.event.events

import com.kisman.cc.event.Event

/**
 * @author _kisman_
 * @since 17:48 of 08.03.2023
 */
open class EventUpdateEntities : Event() {
    class Pre : EventUpdateEntities()
    class Post : EventUpdateEntities()
}