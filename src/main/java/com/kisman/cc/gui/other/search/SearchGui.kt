package com.kisman.cc.gui.other.search

import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.enums.SearchGuiItemsNameModes
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.gui.TextFieldHandler
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d
import net.minecraft.client.gui.GuiScreen

/**
 * @author _kisman_
 * @since 20:21 of 19.05.2022
 */
class SearchGui(
    val setting : Setting, //TODO
    val lastGui : GuiScreen?
) : GuiScreen() {
    var textField : TextFieldHandler? = null

    val comps : ArrayList<Component> = ArrayList()

    var x : Int = 15
    var y : Int = 15
    var width_ : Int = 500
    var height_ : Int = 300

    var drag : Boolean = false
    var open : Boolean = true
    private var dragX : Int = 0
    private var dragY : Int = 0

    var count : Int = 0

    companion object {
        var nameMode = SearchGuiItemsNameModes.None
        var resolutionX : Int = 10
    }

    init {
       init()
    }

    private fun init() {
        textField = TextFieldHandler(
            x + HalqGui.height,
            y + HalqGui.headerOffset,
            width_ - HalqGui.headerOffset * 2,
            HalqGui.height
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (drag) {
            x = mouseX - dragX
            y = mouseY - dragY
        }

        Render2DUtil.drawRectWH(
            x.toDouble(),
            y.toDouble(),
            width_.toDouble(),
            height_.toDouble(),
            HalqGui.getGradientColour(count).rgb
        )
        if (HalqGui.shadow) {
            Render2DUtil.drawAbstract(
                AbstractGradient(
                    Vec4d(
                        doubleArrayOf(
                            (x - HalqGui.headerOffset).toDouble(),
                            y.toDouble()
                        ),
                        doubleArrayOf(x.toDouble(), y.toDouble()),
                        doubleArrayOf(x.toDouble(), y + height_.toDouble()),
                        doubleArrayOf((x - HalqGui.headerOffset).toDouble(), y.toDouble() + height_.toDouble())
                    ),
                    ColorUtils.injectAlpha(HalqGui.getGradientColour(count).color, 0),
                    HalqGui.getGradientColour(count).color
                )
            )
            Render2DUtil.drawAbstract(
                AbstractGradient(
                    Vec4d(
                        doubleArrayOf(
                            (x + width_).toDouble(),
                            y.toDouble()
                        ),
                        doubleArrayOf((x + width_ + HalqGui.headerOffset).toDouble(), y.toDouble()),
                        doubleArrayOf(
                            (x + width_ + HalqGui.headerOffset).toDouble(),
                            y + height_.toDouble()
                        ),
                        doubleArrayOf((x + width_).toDouble(), y.toDouble() + height_.toDouble())
                    ),
                    HalqGui.getGradientColour(count).color,
                    ColorUtils.injectAlpha(HalqGui.getGradientColour(count).color, 0)
                )
            )
        }

        HalqGui.drawString(
            "Items/Blocks of ${setting.name}.", x, y, width_, height_
        )


        if(open) {
            textField?.drawTextBox()
            //TODO
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        textField?.textboxKeyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(isMouseOnHeader(mouseX, mouseY)) {
            drag = true
            dragX = mouseX - x
            dragY = mouseY - y
            open = (mouseButton == 1)
        }
        textField?.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        drag = false
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    override fun initGui() {
        init()
    }

    override fun onGuiClosed() {
        if(lastGui != null) mc.displayGuiScreen(lastGui)
    }

    override fun doesGuiPauseGame(): Boolean {
        return true
    }

    private fun isMouseOnHeader(x : Int, y : Int) : Boolean {
        return x > this.x && x < this.x + this.width_ && y > this.y && y < this.y + HalqGui.height
    }
}