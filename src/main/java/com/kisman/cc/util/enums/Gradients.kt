package com.kisman.cc.util.enums

import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.ColorUtils
import kotlin.math.abs

/**
 * @author _kisman_
 * @since 18:54 of 08.01.2023
 */
enum class Gradients(
    val getter : IGradient
) {
    None(object : IGradient {
        override fun get(
            delay : Int,
            vararg values : Any
        ): Int = delay
    }),
    Rainbow(object : IGradient {
        override fun get(
            delay : Int,
            vararg values : Any
        ) = if(values.size == 2) {
            ColorUtils.injectAlpha(ColorUtils.rainbow(delay, values[0] as Float, values[1] as Float), 255).rgb
        } else {
            ColorUtils.injectAlpha(ColorUtils.rainbow(delay, values[0] as Float, values[1] as Float, values[2] as Float), 255).rgb
        }
    }),
    Astolfo(object : IGradient {
        override fun get(
            delay: Int,
            vararg values: Any
        ) = ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(delay), 255).rgb
    }),
    Pulsive(object : IGradient {
        override fun get(
            delay : Int,
            vararg values : Any
        ) = ColorUtils.twoColorEffect(values[0] as Colour, values[1] as Colour, delay, values[2] as Float).rgb
    })
    ;

    fun get(
        delay : Int,
        sat : Float,
        bright : Float,
        speed : Float?,
        color1 : Colour,
        color2 : Colour
    ) = if (this == Rainbow) {
        if(speed != null) {
            getter.get(delay, sat, bright, speed)
        } else {
            getter.get(delay, sat, bright)
        }
    } else if (this == Pulsive) {
        getter.get(delay, color1, color2, speed ?: 1f)
    } else {
        getter.get(delay)
    }
}

interface IGradient {
    fun get(
        delay : Int,
        vararg values : Any
    ) : Int
}