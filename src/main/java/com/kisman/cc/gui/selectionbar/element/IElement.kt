package com.kisman.cc.gui.selectionbar.element

/**
 * @author _kisman_
 * @since 18:47 of 24.11.2022
 */
interface IElement {
    fun init(x : Int, y : Int)
    fun draw(x : Int, y : Int, mouseX : Int, mouseY : Int)
    fun mouse(button : Int, mouseX : Int, mouseY : Int)
    fun key(key : Int, char : Char)
    fun width() : Int
    fun height() : Int
}