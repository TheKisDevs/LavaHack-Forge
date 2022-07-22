package com.kisman.cc.gui.halq.util

/**
 * @author _kisman_
 * @since 19:34 of 21.07.2022
 */
const val modifier = 5

fun getXOffset(step : Int) : Int {
    return step * modifier
}

fun getModifiedWidth(step : Int, width : Int) : Int {
    return width - getXOffset(step) * 2
}