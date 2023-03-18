package com.kisman.cc.gui.api

import com.kisman.cc.features.module.client.GuiModule
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
    @JvmField protected val ENABLE_ANIMATION2 = AnimationHandler(false)
    @JvmField protected val DISABLE_ANIMATION = AnimationHandler(true)

    private var prevState : Boolean? = null

    fun toggle(
        state : Boolean
    ) {
        var doIt = true
        if(prevState == null){
            prevState = state
            doIt = false
        }
        if (state) {
            if(prevState != state || !doIt) ENABLE_ANIMATION.reset()
            ENABLE_ANIMATION.update()
            ENABLE_ANIMATION2.reset()
            DISABLE_ANIMATION.reset()
        } else {
            DISABLE_ANIMATION.update()
            ENABLE_ANIMATION2.update()
            if (GuiModule.instance.animationCoolAnimation.valBoolean){
                ENABLE_ANIMATION.update()
            } else {
                ENABLE_ANIMATION.reset()
            }
        }
        if(doIt){
            prevState = state
        }
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
        if(GuiModule.instance.animationCoolAnimation.valBoolean){
            HalqGui.drawRectWH2(x + HalqGui.offsetsX, y + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).rgb, ENABLE_ANIMATION.current, ENABLE_ANIMATION2.current)
        } else {
            HalqGui.drawRectWH(x + HalqGui.offsetsX, y + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).rgb, current(state), state)
        }

        toggle(state)
    };
}