package com.kisman.cc.util.enums

import com.kisman.cc.util.TimerUtils
import net.minecraft.util.ResourceLocation

/**
 * @author _kisman_
 * @since 20:23 of 24.10.2022
 */
@Suppress("unused")
enum class CapeEnum(
    val location : String
) {
    Gif("null") {
        var timer = TimerUtils()
        var count = 0

        override fun location() : ResourceLocation {
            if (count > 34) count = 0

            val cape = ResourceLocation("kismancc:cape/rainbow/cape-$count.png")

            if (timer.passedMillis(85)) {
                count++
                timer.reset()
            }

            return cape
        }
    },
    XuluPlus("kismancc:cape/xuluplus/xulupluscape.png"),
    Kuro("kismancc:cape/kuro/kuro.png"),
    Gentle("kismancc:cape/gentlemanmc/GentlemanMC.png"),
    Putin("kismancc:cape/putin/putin.png"),
    Gradient("kismancc:cape/gradient/gradient.png")

    ;

    open fun location() : ResourceLocation = ResourceLocation(location)
}