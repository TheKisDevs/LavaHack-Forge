package com.kisman.cc.gui.api

import com.kisman.cc.util.Colour

/**
 * @author _kisman_
 * @since 19:27 of 25.06.2022
 */
interface ColorChanger : Component {
    fun setColor(color : Colour)
    fun getColor() : Colour?
    fun setPicking(picking : Boolean)
}