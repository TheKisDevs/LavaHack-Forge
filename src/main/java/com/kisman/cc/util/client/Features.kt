@file:Suppress("PrivatePropertyName", "FunctionName")

package com.kisman.cc.util.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventInput
import com.kisman.cc.features.hud.modules.ArrayListModule
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.client.interfaces.IBindable
import com.kisman.cc.util.enums.BindType
import com.kisman.cc.util.math.Animation
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listenable
import me.zero.alpine.listener.Listener
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

interface IFeature : Listenable, IBindable

abstract class DisplayableFeature : IFeature {
    private val ENABLE_ANIMATION = ArrayListAnimation(false)//AnimationExtended({ ArrayListModule.ANIMATION_EASING }, { ArrayListModule.ANIMATION_LENGTH }, false)

    private val DISABLE_ANIMATION = ArrayListAnimation(true)//AnimationExtended({ ArrayListModule.ANIMATION_EASING }, { ArrayListModule.ANIMATION_LENGTH }, true)

    fun ENABLE_ANIMATION() = ENABLE_ANIMATION
    fun DISABLE_ANIMATION() = DISABLE_ANIMATION

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

class ArrayListAnimation(
    reverse : Boolean
) : Animation(
    if(reverse) 1.0 else 0.0,
    if(reverse) 0.0 else 1.0,
    750
) {
    override fun update() {
        time = ArrayListModule.ANIMATION_LENGTH

        super.update()
    }

    override fun getCurrent() : Double = ArrayListModule.ANIMATION_EASING.task.doTask(super.getCurrent())
}