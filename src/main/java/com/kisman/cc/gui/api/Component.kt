package com.kisman.cc.gui.api

import com.kisman.cc.gui.halq.HalqGui

@Suppress("UNUSED_PARAMETER")
interface Component {
    fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        HalqGui.currentComponent = this
    }

    fun mouseClicked(mouseX : Int, mouseY :  Int, button : Int) { }
    fun mouseReleased(mouseX : Int, mouseY : Int, mouseButton : Int) { }
    fun updateComponent(x : Int, y : Int) { }
    fun keyTyped(typedChar : Char, key : Int) { }
    fun setOff(newOff : Int) { }

    val height : Int
        get() = HalqGui.height
    val rawHeight : Int
        get() = HalqGui.height
    var width : Int
    var count : Int

    var x : Int
        get() = 0
        set(value) {}

    var y : Int
        get() = 0
        set(value) {}

    var layer : Int
        get() = 0
        set(value) {}

    fun visible() : Boolean = true
}