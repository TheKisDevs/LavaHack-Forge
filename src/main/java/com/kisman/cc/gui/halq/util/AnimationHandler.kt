package com.kisman.cc.gui.halq.util

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.util.math.Animation

/**
 * Only for 0/1 min/max values
 *
 * @author _kisman_
 * @since 18:31 of 18.02.2023
 */
class AnimationHandler(
    reverse : Boolean
) : Animation(
    if(reverse) 1.0 else 0.0,
    if(reverse) 0.0 else 1.0,
    HalqGui.animationSpeed.toLong()
) {
    override fun update() {
        time = HalqGui.animationSpeed.toLong()

        super.update()
    }

    override fun getCurrent() : Double = HalqGui.animationEasing.task.doTask(super.getCurrent())
}