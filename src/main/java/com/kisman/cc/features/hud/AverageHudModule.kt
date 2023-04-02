package com.kisman.cc.features.hud

import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * @author _kisman_
 * @since 13:04 of 05.03.2023
 */
@Suppress("LeakingThis")
open class AverageHudModule(
    name : String,
    desc : String,
    private val text : () -> (String)
) : ShaderableHudModule(
    name,
    desc,
    true,
    false,
    false
) {
    private val color = colors()

    override fun draw() {
        val text = text()

        setW(CustomFontUtil.getStringWidth(text).toDouble())
        setH(CustomFontUtil.getFontHeight().toDouble())

        shaderRender = Runnable { drawStringWithShadow(text, getX(), getY(), color.color(1, getY().toInt()).rgb) }
    }
}