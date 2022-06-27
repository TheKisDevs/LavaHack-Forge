package com.kisman.cc.gui.api

/**
 * @author _kisman_
 * @since 13:29 of 27.06.2022
 */
interface Draggable {
    fun getX() : Double
    fun getY() : Double
    fun getW() : Double
    fun getH() : Double

    fun setX(x : Double)
    fun setY(y : Double)
    fun setW(w : Double)
    fun setH(h : Double)
}