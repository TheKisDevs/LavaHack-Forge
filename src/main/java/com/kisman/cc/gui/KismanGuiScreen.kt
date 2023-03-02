package com.kisman.cc.gui

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.objects.screen.AbstractGradient
import com.kisman.cc.util.render.objects.screen.Vec4d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

/**
 * @author _kisman_
 * @since 22:10 of 24.11.2022
 */
open class KismanGuiScreen : GuiScreen() {
    override fun initGui() {
        super.initGui()

        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.initGui()
        }
    }

    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        mouseButton : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.mouseClicked(mouseButton, mouseX, mouseY)
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    fun drawSelectionBar(
        mouseX : Int,
        mouseY : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.drawScreen(mouseX, mouseY)
        }
    }

    override fun keyTyped(
        typedChar : Char,
        keyCode : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.keyTyped(typedChar, keyCode)
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int,
        partialTicks : Float
    ) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        drawSelectionBar(mouseX, mouseY)
    }

    fun drawScreenPre() {
        drawGradientBackground()
    }

    private fun drawGradientBackground() {
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

    private fun getStartColor() : Color = when(Config.instance.ggbStartColorMode.valEnum as Config.GGBColorMode) {
        Config.GGBColorMode.Custom -> Config.instance.ggbStartColor.colour.color
        Config.GGBColorMode.SynsWithGui -> {
            if(Minecraft.getMinecraft().currentScreen is HalqGui) {
                ColorUtils.injectAlpha(HalqGui.getGradientColour(0).rgb, Config.instance.ggbStartColor.colour.a)
            } else {
                Config.instance.ggbStartColor.colour.color
            }
        }
    }

    private fun getEndColor() : Color = when(Config.instance.ggbEndColorMode.valEnum as Config.GGBColorMode) {
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