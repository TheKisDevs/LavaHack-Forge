package com.kisman.cc.util.render.customfont

import com.kisman.cc.features.module.client.CustomFontModule
import com.kisman.cc.util.enums.FontStyles
import java.awt.Font
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 10:40 of 28.05.2022
 */
open class ExtendedFontRenderer(
    val font : Font
) : AbstractFontRenderer() {
    private val plain = CustomFontRenderer(font.deriveFont(FontStyles.Plain.style), true, true)
    private val bold = CustomFontRenderer(font.deriveFont(FontStyles.Bold.style), true, true)
    private val italic = CustomFontRenderer(font.deriveFont(FontStyles.Italic.style), true, true)
    private val both = CustomFontRenderer(font.deriveFont(FontStyles.Both.style), true, true)

    private val style = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.style.valEnum as FontStyles else FontStyles.Plain }
    private val test = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.test.valBoolean else false }
    private val test2 = Supplier { if(CustomFontModule.instance != null) CustomFontModule.instance.test2.valBoolean else false }

    private var offset = 2

    open fun getCurrentFont() : CustomFontRenderer {
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

    override fun drawChar(data: Array<CustomFont.CharData>, c: Char, x: Float, y: Float) {
        getCurrentFont().drawChar(data, c, x, y)
    }

    override fun getHeight() : Int {
        return (getCurrentFont().fontHeight - (if(test.get()) 0 else 8)) / 2
    }

    override fun drawStringWithShadow(text: String, x: Int, y: Int, color: Int) {
        getCurrentFont().drawStringWithShadow(text, x.toDouble(), getY(y).toDouble(), color)
    }

    override fun drawLine(x: Int, y: Int, x1: Int, y1: Int) {
        getCurrentFont().drawLine(x.toDouble(), y.toDouble(), x1.toDouble(), y1.toDouble())
    }

    override fun setFractionalMetrics(fractionalMetrics: Boolean) {
        getCurrentFont().isFractionalMetrics = fractionalMetrics
    }

    override fun setAntiAlias(antiAlias: Boolean) {
        getCurrentFont().setAntiAlias(antiAlias)
    }

    override fun getFractionMetrics() : Boolean {
        return getCurrentFont().fractionalMetrics
    }

    override fun getAntiAlias() : Boolean {
        return getCurrentFont().antiAlias
    }

    override fun setFont(font: Font) {
        getCurrentFont().font = font
    }

    override fun getStringWidth(text: String) : Int {
        return getCurrentFont().getStringWidth(text)
    }

    override fun getStringHeight(text : String) : Int = (getHeight() + offset) * text.split("\n").size

    override fun drawString(text: String, x: Double, y: Double, color: Int, shadow: Boolean) : Float {
        return getCurrentFont().drawString(text, x, getY(y.toInt()).toDouble(), color, shadow)
    }

    override fun drawString(text: String, x: Double, y: Double, color: Int): Float {
        return drawString(text, x, getY(y.toInt()).toDouble(), color, false)
    }

    override fun drawCenteredString(text: String, x: Float, y: Float, color: Int) {
        getCurrentFont().drawCenteredString(text, x, getY(y.toInt()).toFloat(), color)
    }

    override fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int) {
        getCurrentFont().drawCenteredStringWithShadow(text, x, getY(y.toInt()).toFloat(), color)
    }

    override fun getMultiLineOffset() : Int = offset

    override fun setMultiLineOffset(
        offset : Int
    ) {
        this.offset = offset
        getCurrentFont().offset = getHeight() + offset
    }

    override fun setupTexture() {
        plain.setupTexture()
        bold.setupTexture()
        italic.setupTexture()
        both.setupTexture()
    }
}