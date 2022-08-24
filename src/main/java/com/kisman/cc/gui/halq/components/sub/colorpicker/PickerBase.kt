package com.kisman.cc.gui.halq.components.sub.colorpicker

import com.kisman.cc.gui.api.ColorChanger
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Render2DUtil
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

/**
 * @author _kisman_
 * @since 12:47 of 25.06.2022
 */
class PickerBase(
    val setting : Setting,
    var x_ : Int,
    var y_ : Int,
    var offset : Int,
    var count_ : Int
) : ColorChanger {
    private var color : Colour? = null

    private var layer_ : Int = 0
    private var width_ : Int = 0

    private var picking_ = false

    override fun setPicking(picking: Boolean) {
        picking_ = picking
    }

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glBegin(GL11.GL_POLYGON)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glVertex2f(x_.toFloat(), (y_ + offset).toFloat())
        GL11.glVertex2f(x_.toFloat(), (y_ + width_).toFloat())
        GL11.glColor4f(color?.r1!!, color?.g1!!, color?.b1!!, color?.a1!!)
        GL11.glVertex2f((x_ + width_).toFloat(), (y_ + width_).toFloat())
        GL11.glVertex2f((x_ + width_).toFloat(), (y_ + offset).toFloat())

        GL11.glEnd()
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glBegin(GL11.GL_POLYGON)

        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f)
        GL11.glVertex2f(x_.toFloat(), (y_ + offset).toFloat())
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
        GL11.glVertex2f(x_.toFloat(), (y_ + width_).toFloat())
        GL11.glVertex2f((x_ + width_).toFloat(), (y_ + width_).toFloat())
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f)
        GL11.glVertex2f((x_ + width_).toFloat(), (y_ + offset).toFloat())

        GL11.glEnd()
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)

        if(isMouseOnButton(mouseX, mouseY)) {
            val restrictedX = min(max(x_, mouseX), x_ + width_)
            val restrictedY = min(max(y_ + offset, mouseX), y_ + offset + width_)
            color?.saturation = ((restrictedX - x_) / width_).toFloat()
            color?.brightness = 1f - (restrictedY - y_) / width_
        }

        val cursorX = x + color?.RGBtoHSB()!![1] * width_
        val cursorY = (y_ + offset + width_) - color?.RGBtoHSB()!![2] * width_

        Render2DUtil.drawRectWH(cursorX - 2.0, cursorY - 2.0, 4.0, 4.0, -1)

        setting.colour = color
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if(button == 0 && isMouseOnButton(mouseX, mouseY)) {
            picking_ = true
        }
    }

    private fun isMouseOnButton(x : Int, y : Int) : Boolean {
        return x >= x_ && x <= x_ + width_ && y >= this.y_ + offset && y <= this.y_ + offset + height
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        picking_ = false
    }

    override fun updateComponent(x: Int, y: Int) {
        x_ = x
        this.y_ = y
    }

    override fun setOff(newOff: Int) {
        offset = newOff
    }

    override fun setCount(count: Int) {
        count_ = count
    }

    override fun getHeight(): Int {
        return width_
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