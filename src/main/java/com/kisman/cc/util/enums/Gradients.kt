package com.kisman.cc.util.enums

import com.kisman.cc.util.render.ColorUtils

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
        ) : Int = delay
    }),
    Rainbow(object : IGradient {
        override fun get(
            delay : Int,
            vararg values : Any
        ) : Int = ColorUtils.injectAlpha(ColorUtils.rainbow(delay, values[0] as Float, 1f), 255).rgb
    }),
    Astolfo(object : IGradient {
        override fun get(
            delay : Int,
            vararg values : Any
        ) : Int = ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(delay), 255).rgb
    })
}

interface IGradient {
    fun get(
        delay : Int,
        vararg values : Any
    ) : Int
}