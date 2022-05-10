package com.kisman.cc.gui.halq.component.components.sub

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.component.Component
import com.kisman.cc.gui.halq.util.LayerMap
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Render2DUtil
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.objects.AbstractGradient
import com.kisman.cc.util.render.objects.Vec4d

class GroupButton(
        val setting : SettingGroup,
        var x_ : Int,
        var y : Int,
        var offset : Int,
        var count_ : Int
) : Component() {
    val comps : ArrayList<Component> = ArrayList()

    var layer_ : Int = 0
    var width_ : Int = 0

    var open : Boolean = false

    init {
        if(setting.settings.isNotEmpty()) {
            var offsetY = offset + HalqGui.height
            var count1 = 0

            for (setting_ in setting.settings) {
                if (setting_ == null) continue
                if (setting_.isSlider) {
                    comps.add(Slider(setting_, x, y, offsetY, count1++))
                    offsetY += HalqGui.height
                }
                if (setting_.isCheck) {
                    comps.add(CheckBox(setting_, x, y, offsetY, count1++))
                    offsetY += HalqGui.height
                }
                if (setting_.isBind) {
                    comps.add(BindButton(setting_, x, y, offsetY, count1++))
                    offsetY += HalqGui.height
                }
                if (setting_.isColorPicker) {
                    comps.add(ColorButton(setting_, x, y, offsetY, count1++))
                    offsetY += HalqGui.height
                }
            }

            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    comp.layer = 2
                    comp.setWidth(70)
                }
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        if (HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(
                    x.toDouble(),
                    (y + offset).toDouble(),
                    width_.toDouble(),
                    HalqGui.height.toDouble(),
                    HalqGui.backgroundColor.rgb
            )
            Render2DUtil.drawAbstract(
                    AbstractGradient(
                            Vec4d(
                                    doubleArrayOf(
                                            x.toDouble(),
                                            (y + offset).toDouble()
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset).toDouble()
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset + HalqGui.height).toDouble()
                                    ),
                                    doubleArrayOf(
                                            x.toDouble(),
                                            (y + offset + HalqGui.height).toDouble()
                                    )
                            ),
                            ColorUtils.injectAlpha(
                                    HalqGui.backgroundColor,
                                    1
                            ),
                            HalqGui.getGradientColour(count).color
                    )
            )
            Render2DUtil.drawAbstract(
                    AbstractGradient(
                            Vec4d(
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset).toDouble()
                                    ),
                                    doubleArrayOf(
                                            (x + width_).toDouble(),
                                            (y + offset).toDouble()
                                    ),
                                    doubleArrayOf(
                                            (x + width_).toDouble(),
                                            (y + offset + HalqGui.height).toDouble()
                                    ),
                                    doubleArrayOf(
                                            (x + width_ / 2).toDouble(),
                                            (y + offset + HalqGui.height).toDouble()
                                    )
                            ),
                            HalqGui.getGradientColour(count).color,
                            ColorUtils.injectAlpha(
                                    HalqGui.backgroundColor,
                                    1
                            )
                    )
            )
        } else Render2DUtil.drawRectWH(
                x.toDouble(),
                (y + offset).toDouble(),
                width_.toDouble(),
                height.toDouble(),
                HalqGui.getGradientColour(count).rgb
        )

        HalqGui.drawString("${setting.name}...", x, y + offset, width_, HalqGui.height)

        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.drawScreen(mouseX, mouseY)
                }
            }
        }
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

    override fun updateComponent(x: Int, y: Int) {
        this.x_ = x
        this.y = y
        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    comp.updateComponent(
                            x + LayerMap.getLayer(comp.layer).modifier,
                            y
                    )
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, key: Int) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) continue
                comp.keyTyped(typedChar, key)
            }
        }
    }

    override fun setOff(newOff: Int) {
        this.offset = newOff
    }

    override fun setCount(count: Int) {
        this.count_ = count
    }

    override fun getHeight(): Int {
        var height = HalqGui.height
        if(comps.isNotEmpty()) {
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
        this.width_ = width
    }

    override fun setX(x: Int) {
        this.x_ = x
    }

    override fun getX(): Int {
        return this.x_
    }

    override fun setLayer(layer: Int) {
        this.layer_ = layer
    }

    override fun getLayer(): Int {
        return this.layer_
    }

    override fun visible(): Boolean {
        return setting.isVisible
    }

    private fun isMouseOnButton(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y + offset && y < this.y + offset + HalqGui.height
    }

    private fun isMouseOnButton2(x: Int, y: Int): Boolean {
        return x > x_ && x < x_ + width_ && y > this.y + offset && y < this.y + offset + height
    }
}