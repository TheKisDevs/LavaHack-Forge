package com.kisman.cc.util.render.customfont

import com.kisman.cc.features.module.client.CustomFontModule
import com.kisman.cc.util.enums.FontStyles
import com.kisman.cc.util.render.customfont.CustomFont.CharData
import java.awt.Font
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 14:59 of 07.06.2022
 */
class FallbackableFont(
    val font : Font,
    val fallbackFont: Font
) : AbstractFontRenderer() {
    private val plain = CustomFontRenderer(font.deriveFont(FontStyles.Plain.style), true, true)
    private val bold = CustomFontRenderer(font.deriveFont(FontStyles.Bold.style), true, true)
    private val italic = CustomFontRenderer(font.deriveFont(FontStyles.Italic.style), true, true)
    private val both = CustomFontRenderer(font.deriveFont(FontStyles.Both.style), true, true)

    private val plainF = CustomFontRenderer(fallbackFont.deriveFont(FontStyles.Plain.style), true, true)
    private val boldF = CustomFontRenderer(fallbackFont.deriveFont(FontStyles.Bold.style), true, true)
    private val italicF = CustomFontRenderer(fallbackFont.deriveFont(FontStyles.Italic.style), true, true)
    private val bothF = CustomFontRenderer(fallbackFont.deriveFont(FontStyles.Both.style), true, true)

    private val style = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.style.valEnum as FontStyles else FontStyles.Plain }
    private val test = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.test.valBoolean else false }
    private val test2 = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.test2.valBoolean else false }

    private val data = arrayOfNulls<CharData>(256)
    private val fallbackData = arrayOfNulls<CharData>(1104)

    /*init {
        plain.charData = data
        bold.charData = data
        italic.charData = data
        both.charData = data

        plainF.charData = fallbackData
        boldF.charData = fallbackData
        italicF.charData = fallbackData
        bothF.charData = fallbackData
    }*/

    private fun getCurrentFont() : CustomFontRenderer {
        return when(style.get()) {
            FontStyles.Plain -> plain
            FontStyles.Bold -> bold
            FontStyles.Italic -> italic
            FontStyles.Both -> both
        }
    }

    private fun getY(y : Int) : Int {
        return if(test2.get()) {
            if(test.get()) {
                y + 2
            } else {
                y - 1
            }
        } else {
            y
        }
    }


    private fun getCurrentFallbackFont() : CustomFontRenderer {
        return when(style.get()) {
            FontStyles.Plain -> plainF
            FontStyles.Bold -> boldF
            FontStyles.Italic -> italicF
            FontStyles.Both -> bothF
        }
    }

    override fun drawChar(data: Array<CustomFont.CharData>, c: Char, x: Float, y: Float) {
    }

    override fun getHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun drawStringWithShadow(text: String, x: Int, y: Int, color: Int) {
        TODO("Not yet implemented")
    }

    override fun drawLine(x: Int, y: Int, x1: Int, y1: Int) {
        TODO("Not yet implemented")
    }

    override fun setFractionalMetrics(fractionalMetrics: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setAntiAlias(antiAlias: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getFractionMetrics(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAntiAlias(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFont(font: Font) {
        TODO("Not yet implemented")
    }

    override fun getMultiLineOffset(): Int {
        TODO("Not yet implemented")
    }

    override fun setMultiLineOffset(offset: Int) {
        TODO("Not yet implemented")
    }

    override fun getStringWidth(text: String): Int {
        TODO("Not yet implemented")
    }

    override fun getStringHeight(text: String): Int {
        TODO("Not yet implemented")
    }

    override fun drawString(text: String, x: Double, y: Double, color: Int, shadow: Boolean): Float {
        var x1 = x
        for(ch in text.toCharArray()) {
            if(font.canDisplay(ch)) {
                x1 += getCurrentFont().drawString(ch.toString(), x1, y, color, shadow)
            } else {
                x1 += getCurrentFallbackFont().drawString(ch.toString(), x1, y, color, shadow)
            }
        }
        return x1.toFloat()
    }

    override fun drawString(text: String, x: Double, y: Double, color: Int): Float {
        TODO("Not yet implemented")
    }

    override fun drawCenteredString(text: String, x: Float, y: Float, color: Int) {
        TODO("Not yet implemented")
    }

    override fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int) {
        TODO("Not yet implemented")
    }

    override fun setupTexture() {
        TODO("Not yet implemented")
    }
}