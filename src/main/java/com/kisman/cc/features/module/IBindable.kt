package com.kisman.cc.features.module

import org.lwjgl.input.Keyboard

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

    companion object {
        fun getKey(bindable : IBindable) : Int {
            return when(bindable.getType()) {
                BindType.Keyboard -> bindable.getKeyboardKey()
                BindType.Mouse -> bindable.getMouseButton()
            }
        }

        fun getName(bindable : IBindable) : String {
            return when(bindable.getType()) {
                BindType.Keyboard -> Keyboard.getKeyName(bindable.getKeyboardKey())
                BindType.Mouse -> "Button_${bindable.getMouseButton()}"
            }
        }

        fun valid(bindable : IBindable) : Boolean {
            return when(bindable.getType()) {
                BindType.Keyboard -> bindable.getKeyboardKey() != Keyboard.KEY_NONE && bindable.getKeyboardKey() != Keyboard.KEY_ESCAPE
                BindType.Mouse -> bindable.getMouseButton() > 1
            }
        }
    }
}