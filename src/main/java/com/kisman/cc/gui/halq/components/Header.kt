package com.kisman.cc.gui.halq.components

import com.kisman.cc.features.module.client.Config
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation
import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.util.Colour
import com.kisman.cc.util.collections.Bind
import com.kisman.cc.util.enums.RectSides
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.ShadowRectObject

/**
 * @author _kisman_
 * @since 18:31 of 19.01.2023
 */
class Header(
    private val frame : Frame
) : ShaderableImplementation() {
    override fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        val shaderRunnable1 = Runnable {
            if (HalqGui.shadowRects) {
                val obj = ShadowRectObject(
                    x.toDouble(),
                    y.toDouble(),
                    (x + HalqGui.width).toDouble(),
                    (y + HalqGui.height).toDouble(),
                    HalqGui.getGradientColour(count),
                    HalqGui.getGradientColour(count).withAlpha(0),
                    5.0,
                    listOf(RectSides.Bottom)
                )
                obj.draw()
            } else Render2DUtil.drawRectWH(
                x.toDouble(),
                y.toDouble(),
                HalqGui.width.toDouble(),
                HalqGui.height.toDouble(),
                HalqGui.getGradientColour(count).rgb
            )
        }

        val shaderRunnable2 = Runnable {
            HalqGui.drawString(
                if (frame.customName) frame.name else frame.cat.getName(),
                x,
                y,
                HalqGui.width,
                HalqGui.height
            )

            if (Config.instance.guiRenderSize.valBoolean) {
                HalqGui.drawSuffix(
                    "[" + frame.components.size + "]",
                    if (frame.customName) frame.name else frame.cat.getName(),
                    x.toDouble(),
                    y.toDouble(),
                    HalqGui.width.toDouble(),
                    HalqGui.height.toDouble(),
                    Colour(255, 255, 255, 255),
                    2
                )
            }
        }

        shaderRender = Bind(shaderRunnable1, shaderRunnable2)
    }

    override fun getX() : Int = frame.x
    override fun getY() : Int = frame.y
    override fun getLayer() : Int = 0
}