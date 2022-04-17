package com.kisman.cc.util

class ColourUtilKt {
    companion object {
        fun getDefaultColor(): Colour {
            return Colour(255, 255, 255, 255)
        }

        fun toConfig(color: Colour): String {
            return "${color.r}:${color.g}:${color.b}:${color.a}"
        }

        fun fromConfig(config: String, color: Colour): Colour {
            val split = config.split(':')
            return try {
                Colour(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]))
            } catch (e: NumberFormatException) {
                color
            }
        }
    }
}