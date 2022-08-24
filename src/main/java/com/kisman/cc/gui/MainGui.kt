package com.kisman.cc.gui

import com.kisman.cc.Kisman
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.HalqHudGui
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
import java.awt.Color

class MainGui {
    companion object {
        fun openGui(bar : SelectionBar) {
            when (bar.selection) {
                Guis.ClickGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqGui)
                Guis.CSGOGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.clickGuiNew)
                Guis.HudEditor -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.halqHudGui)
                Guis.Music -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.musicGui)
                Guis.Console -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.consoleGui)
                Guis.PingBypassGui -> Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.pingBypassGui)
            }
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

    class SelectionBar(
            defaultSelection : Guis
    ) {
        var selection : Guis
        val backgroundColor : Colour = Colour(20, 20, 20, 200)
        val offset : Int = 5

        init {
            selection = defaultSelection
        }

        fun drawScreen(mouseX : Int, mouseY : Int) {
            var startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
            Render2DUtil.drawRectWH(startX.toDouble(), 0.0, getSelectionBarWidth().toDouble(), (CustomFontUtil.getFontHeight() + offset * 2).toDouble(), backgroundColor.rgb)

            for(gui in Guis.values()) {
                CustomFontUtil.drawStringWithShadow(
                        gui.displayName,
                        (startX + offset).toDouble(),
                        offset.toDouble(),
                        if(gui == selection) ColorUtils.astolfoColors(100, 100) else -1
                )
                if(Mouse.isButtonDown(0)) {
                    if(mouseX >= startX && mouseX <= startX + offset * 2 + CustomFontUtil.getStringWidth(gui.displayName) && mouseY >= 0 && mouseY <= offset * 2 + CustomFontUtil.getFontHeight(                     )
                    ) {
                        selection = gui
                    }
                }
                startX += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
            }
        }

        fun mouseClicked(mouseX : Int, mouseY : Int) : Boolean {
            /*val startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
            if(mouseX >= startX && mouseX <= startX + getSelectionBarWidth() && mouseY >= 0 && mouseY <= CustomFontUtil.getFontHeight() + offset * 2) {
                for((count, gui) in Guis.values().withIndex()) {
                    if(mouseX >= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) && mouseX <= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) + (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) {
                        println("Gui: ${gui.displayName}")
                        selection = gui
                        return false
                    }
                }
            }
            return true*/
            return true
        }

        private fun getSelectionBarWidth() : Int {
            var width = 0

            for(gui in Guis.values()) {
                width += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
            }

            return width
        }
    }

    enum class Guis(
            val displayName: String
    ) {
        ClickGui("Click Gui"),
        PingBypassGui("Ping Bypass"),
        CSGOGui("CSGO Gui"),
        HudEditor("Hud Editor"),
        Music("Music"),
        Console("Console")
    }
}