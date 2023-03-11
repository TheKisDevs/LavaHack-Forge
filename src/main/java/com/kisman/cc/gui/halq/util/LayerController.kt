package com.kisman.cc.gui.halq.util

import com.kisman.cc.gui.halq.HalqGui

/**
 * @author _kisman_
 * @since 19:34 of 21.07.2022
 */

fun getXOffset(step : Int) : Int {
    return step * HalqGui.layerStepOffset
}

fun getModifiedWidth(step : Int, width : Int) : Int {
    return width - getXOffset(step) * 2
}