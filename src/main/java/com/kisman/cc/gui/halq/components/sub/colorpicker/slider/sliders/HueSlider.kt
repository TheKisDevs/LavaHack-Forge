package com.kisman.cc.gui.halq.components.sub.colorpicker.slider.sliders

import com.kisman.cc.gui.api.ColorChanger
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.sub.colorpicker.slider.ISlider
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Render2DUtil
import net.minecraft.client.gui.Gui
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * @author _kisman_
 * @since 12:49 of 25.06.2022
 */
class HueSlider(
    val setting : Setting,
    var x_ : Int,
    var y : Int,
    var offset : Int,
    var count_ : Int
) : ColorChanger, ISlider {
    private var color : Colour? = null

    private var layer_ : Int = 0
    private var width_ : Int = 0

    private var picking_ = false

    override fun setPicking(picking: Boolean) {
        picking_ = picking
    }

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        for (step in 0..5) {
            val previousStep = Color.HSBtoRGB(step.toFloat() / 6, 1.0f, 1.0f)
            val nextStep = Color.HSBtoRGB((step + 1).toFloat() / 6, 1.0f, 1.0f)
            GL11.glPushMatrix()
            this.gradient(
                x + step * (width_ / 6),
                y + offset,
                x + (step + 1) * (width_ / 6),
                y + offset + height,
                previousStep,
                nextStep
            )
            GL11.glPopMatrix()
        }
        Render2DUtil.drawRectWH((x + width_ * color?.hue!!) - 1.0, y + offset.toDouble(), 2.0, height.toDouble(), -1)

        if(isMouseOnButton(mouseX, mouseY)) {
            val restrictedX = min(max(x_, mouseX), x_ + width_)
            color?.hue = ((restrictedX - x_) / width_).toFloat()
        }

        val cursorX = x + color?.RGBtoHSB()!![1] * width_
        val cursorY = (y + offset + width_) - color?.RGBtoHSB()!![2] * width_

        Render2DUtil.drawRectWH(cursorX - 2.0, cursorY - 2.0, 4.0, 4.0, -1)

        setting.colour = color
    }

    private fun gradient(minX: Int, minY: Int, maxX: Int, maxY: Int, startColor: Int, endColor: Int) {
        val startA = (startColor shr 24 and 0xFF) / 255.0f
        val startR = (startColor shr 16 and 0xFF) / 255.0f
        val startG = (startColor shr 8 and 0xFF) / 255.0f
        val startB = (startColor and 0xFF) / 255.0f
        val endA = (endColor shr 24 and 0xFF) / 255.0f
        val endR = (endColor shr 16 and 0xFF) / 255.0f
        val endG = (endColor shr 8 and 0xFF) / 255.0f
        val endB = (endColor and 0xFF) / 255.0f
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glBegin(GL11.GL_POLYGON)
        run {
            GL11.glColor4f(startR, startG, startB, startA)
            GL11.glVertex2f(minX.toFloat(), minY.toFloat())
            GL11.glVertex2f(minX.toFloat(), maxY.toFloat())
            GL11.glColor4f(endR, endG, endB, endA)
            GL11.glVertex2f(maxX.toFloat(), maxY.toFloat())
            GL11.glVertex2f(maxX.toFloat(), minY.toFloat())
        }
        GL11.glEnd()
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if(button == 0 && isMouseOnButton(mouseX, mouseY)) {
            picking_ = true
        }
    }

    private fun isMouseOnButton(x : Int, y : Int) : Boolean {
        return x >= x_ && x <= x_ + width_ && y >= this.y + offset && y <= this.y + offset + height
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        picking_ = false
    }

    override fun updateComponent(x: Int, y: Int) {
        x_ = x
        this.y = y
    }

    override fun setOff(newOff: Int) {
        offset = newOff
    }

    override fun setCount(count: Int) {
        count_ = count
    }

    override fun getHeight(): Int {
        return HalqGui.height - 3
    }

    override fun getCount(): Int {
        return count_
    }

    override fun setWidth(width: Int) {
        width_ = width
    }

    override fun setX(x: Int) {
        x_ = x
    }

    override fun getX(): Int {
        return x_
    }

    override fun setLayer(layer: Int) {
        layer_ = layer
    }

    override fun getLayer(): Int {
        return layer_
    }

    override fun visible(): Boolean {
        return true//TODO
    }

    override fun setColor(color: Colour) {
        this.color = color
    }

    override fun getColor() : Colour? {
        return color
    }
}