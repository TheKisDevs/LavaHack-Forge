package com.kisman.cc.gui.api

import com.kisman.cc.gui.api.shaderable.ShaderableImplementation
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.util.AnimationHandler

/**
 * @author _kisman_
 * @since 8:17 of 18.03.2023
 */
@Suppress("PropertyName")
abstract class ToggleableImplementation(
    x : Int,
    y : Int,
    count : Int,
    offset : Int,
    layer : Int
) : ShaderableImplementation(
    x,
    y,
    count,
    offset,
    layer
) {
    @JvmField protected val ENABLE_ANIMATION = AnimationHandler(false)
    @JvmField protected val DISABLE_ANIMATION = AnimationHandler(true)

    fun toggle(
        state : Boolean
    ) = if(state) {
        ENABLE_ANIMATION.update()
        DISABLE_ANIMATION.reset()
    } else {
        DISABLE_ANIMATION.update()
        ENABLE_ANIMATION.reset()
    }

    fun current(
        state : Boolean
    ) = (if(state) {
        ENABLE_ANIMATION
    } else {
        DISABLE_ANIMATION
    }).current

    fun drawRect(
        state : Boolean
    ) {
        HalqGui.drawRectWH(x + HalqGui.offsetsX, y + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).rgb, current(state), state)

        toggle(state)
    };
}