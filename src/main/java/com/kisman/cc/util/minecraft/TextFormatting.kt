package com.kisman.cc.util.minecraft

import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 21:05 of 11.11.2022
 */

val DEFAULT_COLOR_FORMATTER = Formatter(TextFormatting.RESET, TextFormatting.RESET.friendlyName, FormatterType.Color)
val DEFAULT_STYLE_FORMATTER = Formatter(TextFormatting.RESET, TextFormatting.RESET.friendlyName, FormatterType.Color)

fun getColorFormatters() : ArrayList<Formatter> {
    val list = ArrayList<Formatter>()

    for(formatting in TextFormatting.values()) {
        if(!formatting.isFancyStyling || formatting.colorIndex == -1) {
            list.add(Formatter(formatting, formatting.friendlyName, FormatterType.Color))
        }
    }

    return list
}

fun getStyleFormatters() : ArrayList<Formatter> {
    val list = ArrayList<Formatter>()

    for(formatting in TextFormatting.values()) {
        if(formatting.isFancyStyling || formatting.colorIndex == -1) {
            list.add(Formatter(formatting, formatting.friendlyName, FormatterType.Style))
        }
    }

    return list
}

class Formatter(
    val original : TextFormatting,
    val display : String,
    val type : FormatterType
) {
    override fun toString() : String = display
}

enum class FormatterType {
    Color,
    Style
}