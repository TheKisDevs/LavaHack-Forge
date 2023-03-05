package com.kisman.cc.features.hud

import com.kisman.cc.settings.util.HudModuleColorPattern
import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * @author _kisman_
 * @since 13:04 of 05.03.2023
 */
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
    private val color = HudModuleColorPattern(this).preInit().init()

    override fun draw() {
        val text = text()

        setW(CustomFontUtil.getStringWidth(text).toDouble())
        setH(CustomFontUtil.getFontHeight().toDouble())

        shaderRender = Runnable { drawStringWithShadow(text, getX(), getY(), color.color().rgb) }
    }
}