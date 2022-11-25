package com.kisman.cc.gui.halq

import com.kisman.cc.Kisman
import com.kisman.cc.gui.selectionbar.element.IElement
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.render.gui.TextFieldHandler
import net.minecraft.client.Minecraft

/**
 * @author _kisman_
 * @since 19:20 of 24.11.2022
 */
class SearchBar : IElement {
    var field : TextFieldHandler? = null

    override fun init(
        x : Int,
        y : Int
    ) {
        field = TextFieldHandler(x + Kisman.instance.selectionBar.offset, y + CustomFontUtil.getFontHeight() / 2, 100, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT)
    }

    override fun draw(
        x : Int,
        y : Int,
        mouseX : Int,
        mouseY : Int
    ) {
        field!!.drawTextBox()
    }

    override fun mouse(
        button : Int,
        mouseX : Int,
        mouseY : Int
    ) {
       field!!.mouseClicked(mouseX, mouseY, button)
    }

    override fun key(
        key : Int,
        char : Char
    ) {
        field!!.textboxKeyTyped(char, key)
    }

    override fun width() : Int = 100 + Kisman.instance.selectionBar.offset * 2

    override fun height() : Int = CustomFontUtil.getFontHeight() + Kisman.instance.selectionBar.offset * 2

    fun text() : String = field?.text ?: "pon4ik"
}