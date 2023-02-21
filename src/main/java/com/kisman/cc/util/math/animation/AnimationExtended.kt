package com.kisman.cc.util.math.animation

import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.math.Animation
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 8:40 of 19.02.2023
 */
class AnimationExtended(
    private val easing : Supplier<EasingEnum.Easing>,//() -> EasingEnum.Easing,
    private val length : Supplier<Long>,//() -> Long,
    reverse : Boolean
) : Animation(
    if(reverse) 1.0 else 0.0,
    if(reverse) 0.0 else 1.0,
    750
) {
    constructor(
        easing : () -> (EasingEnum.Easing),
        length : () -> (Long),
        reverse : Boolean
    ) : this(
        Supplier { easing() },
        Supplier { length() },
        reverse
    )

    override fun update() {
        time = length.get()

        super.update()
    }

    override fun getCurrent() : Double = easing.get().task.doTask(super.getCurrent())
}