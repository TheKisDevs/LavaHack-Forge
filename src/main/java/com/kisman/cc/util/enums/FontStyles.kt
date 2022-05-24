package com.kisman.cc.util.enums

import java.awt.Font

/**
 * @author _kisman_
 * @since 19:00 of 24.05.2022
 */
enum class FontStyles(
    val style : Int
) {
    Plain(Font.PLAIN),
    Bold(Font.BOLD),
    Italic(Font.ITALIC),
    Both(Font.BOLD or Font.ITALIC)
}