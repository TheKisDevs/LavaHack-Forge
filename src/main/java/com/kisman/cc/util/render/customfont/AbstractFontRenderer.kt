package com.kisman.cc.util.render.customfont

import com.kisman.cc.util.render.customfont.CustomFont.CharData
import java.awt.Font

/**
 * @author _kisman_
 * @since 10:44 of 28.05.2022
 */
abstract class AbstractFontRenderer {
    @Throws(ArrayIndexOutOfBoundsException::class) abstract fun drawChar(data : Array<CharData>, c : Char, x : Float, y : Float)

    abstract fun getHeight() : Int

    abstract fun drawStringWithShadow(
        text : String,
        x : Int,
        y : Int,
        color : Int
    )

    abstract fun drawLine(
        x : Int,
        y : Int,
        x1 : Int,
        y1 : Int
    )

    abstract fun setFractionalMetrics(fractionalMetrics : Boolean)
    abstract fun setAntiAlias(antiAlias : Boolean)
    abstract fun getFractionMetrics(): Boolean
    abstract fun getAntiAlias(): Boolean
    abstract fun setFont(font : Font)

    abstract fun getStringWidth(text : String) : Int

    abstract fun drawString(
        text : String,
        x : Double,
        y : Double,
        color : Int,
        shadow : Boolean
    ) : Float

    abstract fun drawString(
        text : String,
        x : Double,
        y : Double,
        color : Int
    ) : Float

    abstract fun drawCenteredString(
        text : String,
        x : Float,
        y : Float,
        color : Int
    )

    abstract fun drawCenteredStringWithShadow(
        text : String,
        x : Float,
        y : Float,
        color : Int
    )
}