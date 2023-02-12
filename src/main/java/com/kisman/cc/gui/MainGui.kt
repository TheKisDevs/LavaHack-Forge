package com.kisman.cc.gui

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.gui.selectionbar.SelectionBar
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

class MainGui {
    companion object {
        fun openGui(bar : SelectionBar) {
            Minecraft.getMinecraft().displayGuiScreen(bar.selection.gui())
        }
    }

    class GuiGradient {
        fun drawScreen(mouseX : Int, mouseY: Int) {
            if(Config.instance.guiGradientBackground.valBoolean) {
                Render2DUtil.drawAbstract(
                    AbstractGradient(
                        Vec4d(
                            doubleArrayOf(0.0, 0.0),
                            doubleArrayOf(ScaledResolution(Minecraft.getMinecraft()).scaledWidth_double, 0.0),
                            doubleArrayOf(
                                ScaledResolution(Minecraft.getMinecraft()).scaledWidth_double,
                                ScaledResolution(Minecraft.getMinecraft()).scaledHeight_double
                            ),
                            doubleArrayOf(0.0, ScaledResolution(Minecraft.getMinecraft()).scaledHeight_double)
                        ),
                        getStartColor(),
                        getEndColor(),
                        true
                    )
                )
            }
        }

        private fun getStartColor() : Color =
            when(Config.instance.ggbStartColorMode.valEnum as Config.GGBColorMode) {
                Config.GGBColorMode.Custom -> Config.instance.ggbStartColor.colour.color
                Config.GGBColorMode.SynsWithGui -> {
                    if(Minecraft.getMinecraft().currentScreen is HalqGui) {
                        ColorUtils.injectAlpha(HalqGui.getGradientColour(0).rgb, Config.instance.ggbStartColor.colour.a)
                    } else {
                        Config.instance.ggbStartColor.colour.color
                    }
                }
            }

        private fun getEndColor() : Color =
            when(Config.instance.ggbEndColorMode.valEnum as Config.GGBColorMode) {
                Config.GGBColorMode.Custom -> Config.instance.ggbEndColor.colour.color
                Config.GGBColorMode.SynsWithGui -> {
                    if(Minecraft.getMinecraft().currentScreen is HalqGui) {
                        ColorUtils.injectAlpha(HalqGui.getGradientColour(0).rgb, Config.instance.ggbEndColor.colour.a)
                    } else {
                        Config.instance.ggbEndColor.colour.color
                    }
                }
            }
    }

}