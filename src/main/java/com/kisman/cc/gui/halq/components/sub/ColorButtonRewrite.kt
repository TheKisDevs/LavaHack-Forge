package com.kisman.cc.gui.halq.components.sub

import com.kisman.cc.gui.api.ColorChanger
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.api.Openable
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.sub.colorpicker.PickerBase
import com.kisman.cc.gui.halq.components.sub.colorpicker.slider.sliders.AlphaSlider
import com.kisman.cc.gui.halq.components.sub.colorpicker.slider.sliders.HueSlider
import com.kisman.cc.gui.halq.util.getXOffset
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d
import java.awt.Color

/**
 * @author _kisman_
 * @since 12:51 of 25.06.2022
 */
class ColorButtonRewrite(
    val setting : Setting,
    var x_ : Int,
    var y_ : Int,
    var offset : Int,
    var count_ : Int
) : Openable {
    private var color: Colour? = null

    private val comps = ArrayList<Component>()
    private var open = false

    private var layer_ : Int = 0
    private var width_ : Int = 0

    init {
        color = setting.colour

        var offsetY = offset + HalqGui.height
        var count1 = 0

        comps.add(PickerBase(setting, x_, y_, offset, count1++))
        offsetY += HalqGui.height
        comps.add(HueSlider(setting, x_, y_, offset, count1++))
        offsetY += HalqGui.height
        comps.add(AlphaSlider(setting, x_, y_, offset, count1++))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        Render2DUtil.drawRectWH(
            x.toDouble(),
            (y_ + offset).toDouble(),
            width_.toDouble(),
            HalqGui.height.toDouble(),
            HalqGui.backgroundColor.rgb
        )
        if (HalqGui.shadowCheckBox) {
            Render2DUtil.drawAbstract(
                AbstractGradient(
                    Vec4d(
                        doubleArrayOf(
                            x.toDouble() + HalqGui.offsets,
                            (y_ + offset).toDouble() + HalqGui.offsets
                        ),
                        doubleArrayOf(
                            (x + width_ / 2).toDouble(),
                            (y_ + offset).toDouble() + HalqGui.offsets
                        ),
                        doubleArrayOf(
                            (x + width_ / 2).toDouble(),
                            (y_ + offset + HalqGui.height).toDouble() - HalqGui.offsets
                        ),
                        doubleArrayOf(
                            x.toDouble() + HalqGui.offsets,
                            (y_ + offset + HalqGui.height).toDouble() - HalqGui.offsets
                        )
                    ),
                    Color(HalqGui.backgroundColor.rgb),
                    color?.color
                )
            )
            Render2DUtil.drawAbstract(
                AbstractGradient(
                    Vec4d(
                        doubleArrayOf(
                            (x + width_ / 2).toDouble(),
                            (y_ + offset).toDouble() + HalqGui.offsets
                        ),
                        doubleArrayOf(
                            (x + width_).toDouble() - HalqGui.offsets,
                            (y_ + offset).toDouble() + HalqGui.offsets
                        ),
                        doubleArrayOf(
                            (x + width_).toDouble() - HalqGui.offsets,
                            (y_ + offset + HalqGui.height).toDouble() - HalqGui.offsets
                        ),
                        doubleArrayOf(
                            (x + width_ / 2).toDouble(),
                            (y_ + offset + HalqGui.height).toDouble() - HalqGui.offsets
                        )
                    ),
                    color?.color,
                    Color(HalqGui.backgroundColor.rgb)
                )
            )
        } else Render2DUtil.drawRectWH(
            x.toDouble() + HalqGui.offsets,
            (y_ + offset).toDouble() + HalqGui.offsets,
            width_.toDouble() - HalqGui.offsets * 2,
            height.toDouble() - HalqGui.offsets * 2,
            color?.rgb!!
        )

        HalqGui.drawString(setting.title, x, y_ + offset, width_, HalqGui.height)

        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible() && comp !is ColorChanger) continue
                    (comp as ColorChanger).setColor(color!!)
                    comp.drawScreen(mouseX, mouseY)
                    color = comp.getColor()
                }
            }
        }

        setting.colour = color
    }

    override fun updateComponent(x: Int, y: Int) {
        this.x_ = x
        this.y_ = y
        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.updateComponent(
                        x + getXOffset(comp.layer),
                        y
                    )
                }
            }
        }
    }

    private fun isMouseOnButton(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y_ + offset && y < this.y_ + offset + HalqGui.height
    }

    private fun isMouseOnButton2(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y_ + offset && y < this.y_ + offset + getHeight1()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if(isMouseOnButton(mouseX, mouseY)) {
            if(button == 0) {
                open = !open
            }
        } else if(isMouseOnButton2(mouseX, mouseY)) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.mouseClicked(mouseX, mouseY, button)
                }
            }
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) continue
                comp.mouseReleased(mouseX, mouseY, mouseButton)
            }
        }
    }

    override fun setOff(newOff: Int) {
        offset = newOff
    }

    override fun setCount(count: Int) {
        count_ = count
    }

    override fun getHeight(): Int {
        return HalqGui.height
    }

    private fun getHeight1(): Int {
        var height = HalqGui.height
        if(open && comps.isNotEmpty()) {
            for(comp in comps) {
                height += comp.height
            }
        }
        return height
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
        return setting.isVisible
    }

    override fun isOpen(): Boolean {
        return open
    }

    override fun getComponents(): ArrayList<Component> {
        return comps
    }
}