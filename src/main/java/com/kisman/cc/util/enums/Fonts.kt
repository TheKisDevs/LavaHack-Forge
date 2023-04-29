package com.kisman.cc.util.enums

import com.kisman.cc.util.render.customfont.AbstractFontRenderer
import com.kisman.cc.util.render.customfont.CustomFontUtil.*
/**
 * @author _kisman_
 * @since 21:36 of 14.09.2022
 */
@Suppress("unused")
enum class Fonts(
    val font : AbstractFontRenderer
) {
    Verdana(verdana18),
    Comfortaa(comfortaa18),
    ComfortaaLight(comfortaal18),
    ComfortaaBold(comfortaab18),
    Consolas(consolas18),
    LexendDeca(lexendDeca18),
    Futura(futura20),
    SfUi(sfui19),
    Century(century18),
    JelleeBold(jelleeb18),
    MinecraftRus(minecraftRus13),
    Poppins(poppinsRegular18)
}