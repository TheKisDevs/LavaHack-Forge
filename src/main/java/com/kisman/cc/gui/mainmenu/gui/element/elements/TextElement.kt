package com.kisman.cc.gui.mainmenu.gui.element.elements

import com.kisman.cc.gui.mainmenu.gui.element.AbstractElement
import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * For client and plugins
 *
 * @author _kisman_
 * @since 18:51 of 15.06.2022
 */
class TextElement(
    val text : String,
    val x : Double,
    val y : Double
) : AbstractElement() {
    override fun draw() {
        CustomFontUtil.drawStringWithShadow(text, x, y, -1)
    }

    override fun getHeight() : Double {
        return CustomFontUtil.getFontHeight().toDouble()
    }
}