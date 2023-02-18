package com.kisman.cc.gui.halq.components.sub

import com.kisman.cc.features.module.client.GuiModule
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.api.Openable
import com.kisman.cc.gui.api.SettingComponent
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.util.getXOffset
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.client.collections.Bind
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d

@Suppress("SENSELESS_COMPARISON")
class GroupButton(
    val setting : SettingGroup,
    x : Int,
    y : Int,
    offset : Int,
    count : Int,
    layer : Int
) : ShaderableImplementation(
    x,
    y,
    count,
    offset,
    layer
), Openable, SettingComponent {
    private val comps : ArrayList<Component> = ArrayList()

    var open : Boolean = false

    init {
        if(setting.settings.isNotEmpty()) {
            var offsetY = offset + HalqGui.height
            var count1 = 0

            for (setting_ in setting.settings) {
                if (setting_ == null) continue
                if (setting_.isGroup && setting_ is SettingGroup) {
                    comps.add(GroupButton(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isCombo) {
                    comps.add(ModeButton(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isSlider) {
                    comps.add(Slider(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isCheck) {
                    comps.add(CheckBox(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isBind) {
                    comps.add(BindButton(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
                if (setting_.isColorPicker) {
                    comps.add(ColorButton(setting_, x, y, offsetY, count1++, layer + 1))
                    offsetY += HalqGui.height
                }
            }
        }
    }

    override fun isOpen() : Boolean = open

    override fun getComponents() : ArrayList<Component> = comps


    override fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        super<Openable>.drawScreen(mouseX, mouseY)

        normalRender = Runnable {
            Render2DUtil.drawRectWH(
                x.toDouble(),
                y.toDouble(),
                width.toDouble(),
                HalqGui.height.toDouble(),
                HalqGui.backgroundColor.rgb
            )
        }

        val shaderRunnable1 = Runnable {
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                    AbstractGradient(
                        Vec4d(
                            doubleArrayOf(
                                x.toDouble() + HalqGui.offsetsX,
                                (y).toDouble() + HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                (x + width / 2).toDouble(),
                                (y).toDouble() + HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                (x + width / 2).toDouble(),
                                (y + HalqGui.height).toDouble() - HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                x.toDouble() + HalqGui.offsetsX,
                                (y + HalqGui.height).toDouble() - HalqGui.offsetsY
                            )
                        ),
                        ColorUtils.injectAlpha(HalqGui.backgroundColor.rgb, GuiModule.instance.minPrimaryAlpha.valInt),
                        HalqGui.getGradientColour(count).color
                    )
                )
                Render2DUtil.drawAbstract(
                    AbstractGradient(
                        Vec4d(
                            doubleArrayOf(
                                (x + width / 2).toDouble(),
                                (y).toDouble() + HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                (x + width).toDouble() - HalqGui.offsetsX,
                                (y).toDouble() + HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                (x + width).toDouble() - HalqGui.offsetsX,
                                (y + HalqGui.height).toDouble() - HalqGui.offsetsY
                            ),
                            doubleArrayOf(
                                (x + width / 2).toDouble(),
                                (y + HalqGui.height).toDouble() - HalqGui.offsetsY
                            )
                        ),
                        HalqGui.getGradientColour(count).color,
                        ColorUtils.injectAlpha(HalqGui.backgroundColor.rgb, GuiModule.instance.minPrimaryAlpha.valInt)
                    )
                )
            } else Render2DUtil.drawRectWH(
                x.toDouble() + HalqGui.offsetsX,
                y.toDouble() + HalqGui.offsetsY,
                width.toDouble() - HalqGui.offsetsX * 2,
                height.toDouble() - HalqGui.offsetsY * 2,
                HalqGui.getGradientColour(count).rgb
            )
        }

        val shaderRunnable2 = Runnable { HalqGui.drawString("${setting.title}...", x, y, width, HalqGui.height) }

        shaderRender = Bind(shaderRunnable1, shaderRunnable2)

        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) continue
                    HalqGui.drawComponent(comp)
                }
            }
        }
    }

    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        button : Int
    ) {
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

    override fun mouseReleased(
        mouseX : Int,
        mouseY : Int,
        mouseButton : Int
    ) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) continue
                comp.mouseReleased(mouseX, mouseY, mouseButton)
            }
        }
    }

    override fun updateComponent(
        x : Int,
        y : Int
    ) {
        super<ShaderableImplementation>.updateComponent(x, y)

        if(open) {
            if(comps.isNotEmpty()) {
                for(comp in comps) {
                    if(!comp.visible()) {
                        continue
                    }

                    comp.updateComponent((x - getXOffset(layer)) + getXOffset(comp.layer), y)
                }
            }
        }
    }

    override fun keyTyped(
        typedChar : Char,
        key : Int
    ) {
        if(comps.isNotEmpty()) {
            for(comp in comps) {
                if(!comp.visible()) {
                    continue
                }

                comp.keyTyped(typedChar, key)
            }
        }
    }

    private fun getHeight1() : Int {
        var height = HalqGui.height

        if(open && comps.isNotEmpty()) {
            height = doIterationFullHeight(comps, HalqGui.height)
        }

        return height
    }

    private fun doIterationFullHeight(
        components : ArrayList<Component>,
        oldHeight : Int
    ) : Int {
        var height = oldHeight

        for(component in components) {
            height += component.height

            if(component is Openable) {
                height = doIterationFullHeight(component.getComponents(), height)
            }
        }

        return height
    }

    override fun visible() : Boolean = setting.isVisible && HalqGui.visible(setting.title) && HalqGui.visible(this)


    override fun isMouseOnButton(
        x : Int,
        y : Int
    ) : Boolean = x > this.x && x < this.x + this.width && y > this.y && y < this.y + HalqGui.height

    private fun isMouseOnButton2(
        x : Int,
        y : Int
    ) : Boolean = x > this.x && x < this.x + this.width && y > this.y && y < this.y + getHeight1()

    override fun setting() : Setting = setting
}