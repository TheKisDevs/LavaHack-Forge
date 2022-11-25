package com.kisman.cc.gui

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.hudeditor.HalqHudGui
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.gui.selectionbar.SelectionBar
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
            /*when (bar.selection) {
                Guis.ClickGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqGui)
                Guis.CSGOGui -> Minecraft.getMinecraft().displayGuiScreen(if(Kisman.instance.haveLoader) Minecraft.getMinecraft().currentScreen else Kisman.instance.clickGuiNew)
                Guis.HudEditor -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqHudGui)
                Guis.Music -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.musicGui)
                Guis.Console -> Minecraft.getMinecraft().displayGuiScreen(if(Kisman.instance.haveLoader) Minecraft.getMinecraft().currentScreen else Kisman.instance.consoleGui)
                Guis.PingBypassGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.pingBypassGui)
//                Guis.NoComGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.noComGui)
            }*/
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

        private fun getStartColor() : Color {
            return when(Config.instance.ggbStartColorMode.valEnum as Config.GGBColorMode) {
                Config.GGBColorMode.Custom -> Config.instance.ggbStartColor.colour.color
                Config.GGBColorMode.SynsWithGui -> {
                    if(Minecraft.getMinecraft().currentScreen is HalqGui || Minecraft.getMinecraft().currentScreen is HalqHudGui) {
                        HalqGui.getGradientColour(0).color
                    } else {
                        Config.instance.ggbStartColor.colour.color
                    }
                }
            }
        }

        private fun getEndColor() : Color {
            return when(Config.instance.ggbEndColorMode.valEnum as Config.GGBColorMode) {
                Config.GGBColorMode.Custom -> Config.instance.ggbEndColor.colour.color
                Config.GGBColorMode.SynsWithGui -> {
                    if(Minecraft.getMinecraft().currentScreen is HalqGui || Minecraft.getMinecraft().currentScreen is HalqHudGui) {
                        HalqGui.getGradientColour(0).color
                    } else {
                        Config.instance.ggbEndColor.colour.color
                    }
                }
            }
        }
    }

}