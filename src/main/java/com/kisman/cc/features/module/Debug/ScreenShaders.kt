package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingEnum
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Render2DUtil
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

/**
 * @author _kisman_
 * @since 1:08 of 24.08.2022
 */
class ScreenShaders : Module(
    "ScreenShaders",
    "Test of new rounded rect & blur shaders",
    Category.DEBUG
) {
    private val shader = SettingEnum("Shader", this, Shader.RoundedRectAlpha).register()

    private val color = register(Setting("Color", this, Colour(255, 255, 255, 255)))

    private val radius = register(Setting("Radius", this, 1.0, 0.1, 5.0, false))
    private val radiusFactor = register(Setting("Radius Factor", this, 0.1, 0.1, 2.0, false))

    private val alphaFactor = register(Setting("Alpha Factor", this, 0.0, 0.0, 1.0, false))

    private val directionX = register(Setting("Direction X", this, 1.0, -2.0, 2.0, false))
    private val directionY = register(Setting("Direction Y", this, 1.0, -2.0, 2.0, false))

    private val test = register(Setting("Test", this, false))
    private val testAlpha = register(Setting("Test Alpha", this, 0.0, 0.0, 255.0, true))

    @SubscribeEvent fun onRender(event : RenderWorldLastEvent) {
        if(test.valBoolean) {
            Render2DUtil.drawRectWH(100.0, 100.0, 100.0, 100.0, Color(255, 255, 255, testAlpha.valInt).rgb)
        }

        when(shader.valEnum) {
            Shader.RoundedRectAlpha -> Render2DUtil.drawRoundedRect(100f, 100f, 200f, 200f, color.colour.rgb, radius.valFloat)
            Shader.RoundedRect -> Render2DUtil.drawRoundedRect1(100f, 100f, 200f, 200f, color.colour.rgb, radius.valFloat, alphaFactor.valFloat)
            Shader.Blur -> Render2DUtil.drawBlur(100f, 100f, 200f, 200f, radius.valFloat, radiusFactor.valFloat, directionX.valFloat, directionY.valFloat)
        }
    }

    private enum class Shader {
        RoundedRectAlpha,
        RoundedRect,
        Blur
    }
}