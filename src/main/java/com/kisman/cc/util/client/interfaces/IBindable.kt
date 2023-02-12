package com.kisman.cc.util.client.interfaces

import com.kisman.cc.util.enums.BindType
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * @author _kisman_
 * @since 17:45 of 16.07.2022
 */
interface IBindable {
    fun getKeyboardKey() : Int
    fun setKeyboardKey(key : Int)

    fun getMouseButton() : Int
    fun setMouseButton(button : Int)

    fun getType() : BindType
    fun setType(type : BindType)

    fun isHold() : Boolean
    fun setHold(hold : Boolean)

    fun getButtonName() : String

    companion object {
        fun getKey(bindable : IBindable) : Int {
            return when(bindable.getType()) {
                BindType.Keyboard -> bindable.getKeyboardKey()
                BindType.Mouse -> bindable.getMouseButton()
            }
        }

        fun getName(bindable : IBindable) : String {
            return when(bindable.getType()) {
                BindType.Keyboard -> if(bindable.getKeyboardKey() == -1) "NONE" else Keyboard.getKeyName(bindable.getKeyboardKey())
                BindType.Mouse -> "Button_${bindable.getMouseButton()}"
            }
        }

        fun valid(bindable : IBindable) : Boolean {
            return when(bindable.getType()) {
                BindType.Keyboard -> bindable.getKeyboardKey() != Keyboard.KEY_NONE && bindable.getKeyboardKey() != Keyboard.KEY_ESCAPE
                BindType.Mouse -> bindable.getMouseButton() > 1
            }
        }

        fun isPressed(bindable : IBindable) : Boolean {
            return when(bindable.getType()) {
                BindType.Keyboard -> Keyboard.isKeyDown(bindable.getKeyboardKey())
                BindType.Mouse -> Mouse.isButtonDown(bindable.getMouseButton())
            }
        }

        @JvmStatic
        fun bindKey(bindable : IBindable, key : Int) {
            bindable.setKeyboardKey(key)
            bindable.setType(BindType.Keyboard)
        }

        @JvmStatic
        fun bindButton(bindable : IBindable, button : Int) {
            bindable.setKeyboardKey(button)
            bindable.setType(BindType.Mouse)
        }
    }
}