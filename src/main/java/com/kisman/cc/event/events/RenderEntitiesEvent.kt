package com.kisman.cc.event.events

import com.kisman.cc.event.Event

/**
 * @author _kisman_
 * @since 18:35 of 17.08.2022
 */
open class RenderEntitiesEvent : Event() {
    class Start : RenderEntitiesEvent()
    class End : RenderEntitiesEvent()
}