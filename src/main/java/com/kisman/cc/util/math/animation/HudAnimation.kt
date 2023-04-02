package com.kisman.cc.util.math.animation

import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.util.math.Animation

/**
 * @author _kisman_
 * @since 5:46 of 29.03.2023
 */
class HudAnimation(
    private val module : () -> (AverageMultiLineHudModule),
    reverse : Boolean
) : Animation(
    if(reverse) 1.0 else 0.0,
    if(reverse) 0.0 else 1.0,
    750
) {
    override fun update() {
        time = module().LENGTH

        super.update()
    }

    override fun getCurrent() : Double = module().EASING.task.doTask(super.getCurrent())
}