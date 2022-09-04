/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui.components

import java.awt.*
import javax.swing.border.Border

class RoundedBorder(
    private val radius: Int,
    var color: Color,
    private val thickness: Float
) : Border {
    override fun getBorderInsets(c: Component?): Insets {
        return Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius)
    }

    override fun isBorderOpaque(): Boolean {
        return true
    }

    override fun paintBorder(
        component: Component?,
        graphics: Graphics,
        x: Int, y: Int,
        width: Int, height: Int
    ) {
        graphics as Graphics2D
        val oldColor = graphics.color
        val oldStroke = graphics.stroke
        graphics.color = this.color
        graphics.stroke = BasicStroke(thickness)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
        graphics.color = oldColor
        graphics.stroke = oldStroke
    }
}