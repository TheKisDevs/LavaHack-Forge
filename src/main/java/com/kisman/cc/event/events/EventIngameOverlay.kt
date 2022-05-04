package com.kisman.cc.event.events

import com.kisman.cc.event.Event

open class EventIngameOverlay : Event() {
    class BossBar : EventIngameOverlay()
    class Portal : EventIngameOverlay()
    class Pumpkin : EventIngameOverlay()
    class Overlay : EventIngameOverlay()
    class Hotbar : EventIngameOverlay()
}