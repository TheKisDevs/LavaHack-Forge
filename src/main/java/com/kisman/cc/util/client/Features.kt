@file:Suppress("PrivatePropertyName", "FunctionName", "PropertyName")

package com.kisman.cc.util.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventInput
import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.features.hud.modules.ArrayList2
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.client.interfaces.IBindable
import com.kisman.cc.util.enums.BindType
import com.kisman.cc.util.math.animation.HudAnimation
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listenable
import me.zero.alpine.listener.Listener
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

interface IFeature : Listenable, IBindable

open class AnimateableFeature(
    module : AverageMultiLineHudModule? = null
) {
    open val ENABLE_ANIMATION = HudAnimation({ module ?: ArrayList2.instance }, false)
    open val DISABLE_ANIMATION = HudAnimation({ module ?: ArrayList2.instance }, true)

    fun ENABLE_ANIMATION() = ENABLE_ANIMATION
    fun DISABLE_ANIMATION() = DISABLE_ANIMATION
}

abstract class DisplayableFeature : AnimateableFeature(), IFeature {
    abstract fun onInputEvent()

    private val onKey = Listener<EventInput.Keyboard>(EventHook {
        if(mc.player == null || mc.world == null) {
            return@EventHook
        }

        if(getType() == BindType.Keyboard && Keyboard.isCreated()) {
            val key = Keyboard.getEventKey()

            if(key > 1 && getKeyboardKey() == key) {
                if(Keyboard.getEventKeyState()) {
                    onInputEvent()
                } else if(isHold()) {
                    onInputEvent()
                }
            }
        }
    })

    private val onMouse = Listener<EventInput.Mouse>(EventHook {
        if(mc.player == null || mc.world == null) {
            return@EventHook
        }

        if(getType() == BindType.Mouse && Mouse.isCreated()) {
            val button = Mouse.getEventButton()

            if(button > 1 && getMouseButton() == button) {
                if(Mouse.getEventButtonState()) {
                    onInputEvent()
                } else if(isHold()) {
                    onInputEvent()
                }
            }
        }
    })

    init {
        Kisman.EVENT_BUS.subscribe(onKey)
        Kisman.EVENT_BUS.subscribe(onMouse)
    }
}