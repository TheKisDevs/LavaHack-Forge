package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.shader.shaders.*
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.Shaders
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.shader.*
import java.awt.Color

/**
 * @author _kisman_
 * @since 11:32 of 14.01.2023
 */
class ShaderPattern(
    module : Module
) : AbstractPattern<ShaderPattern>(
    module
) {
    @JvmField val mode = setupEnum(SettingEnum("Mode", module, Shaders.AQUA))

    private val config = setupGroup(SettingGroup(Setting("Config", module)))

    private val animationSpeed = setupSetting(config.add(Setting("Animation Speed", module, 0.0, 0.0, 10.0, false)))

    private val blur = setupSetting(config.add(Setting("Blur", module, true)))
    private val radius = setupSetting(config.add(Setting("Radius", module, 2.0, 0.1, 10.0, false)))
    private val mix = setupSetting(config.add(Setting("Mix", module, 1.0, 0.0, 1.0, false)))
    @JvmField val red = setupSetting(config.add(Setting("Red", module, 1.0, 0.0, 1.0, false)))
    @JvmField val green = setupSetting(config.add(Setting("Green", module, 1.0, 0.0, 1.0, false)))
    @JvmField val blue = setupSetting(config.add(Setting("Blue", module, 1.0, 0.0, 1.0, false)))
    private val rainbow = setupSetting(config.add(Setting("RainBow", module, true)))
    private val delay = setupSetting(config.add(Setting("Delay", module, 100.0, 1.0, 2000.0, true)))
    private val saturation = setupSetting(config.add(Setting("Saturation", module, 36.0, 0.0, 100.0, NumberType.PERCENT)))
    private val brightness = setupSetting(config.add(Setting("Brightness", module, 100.0, 0.0, 100.0, NumberType.PERCENT)))

    private val quality = setupSetting(config.add(Setting("Quality", module, 1.0, 0.0, 20.0, false)))
    private val gradientAlpha = setupSetting(config.add(Setting("Gradient Alpha", module, false)))
    private val alphaGradient = setupSetting(config.add(Setting("Alpha Gradient Value", module, 255.0, 0.0, 255.0, true)))
    private val duplicateOutline = setupSetting(config.add(Setting("Duplicate Outline", module, 1.0, 0.0, 20.0, false)))
    private val moreGradientOutline = setupSetting(config.add(Setting("More Gradient", module, 1.0, 0.0, 10.0, false)))
    private val creepyOutline = setupSetting(config.add(Setting("Creepy", module, 1.0, 0.0, 20.0, false)))
    private val alpha = setupSetting(config.add(Setting("Alpha", module, 1.0, 0.0, 1.0, false)))
    private val numOctavesOutline = setupSetting(config.add(Setting("Num Octaves", module, 5.0, 1.0, 30.0, true)))
    private val speedOutline = setupSetting(config.add(Setting("Speed", module, 0.1, 0.001, 0.1, false)))

    private val rainbowSpeed = setupSetting(config.add(Setting("Rainbow Speed", module, 0.4, 0.0, 1.0, false)))
    private val rainbowStrength = setupSetting(config.add(Setting("Rainbow Strength", module, 0.3, 0.0, 1.0, false)))
    private val rainbowSaturation = setupSetting(config.add(Setting("Rainbow Saturation", module, 0.5, 0.0, 1.0, false)))

    private val color1 = setupSetting(config.add(Setting("Color 1", module, Colour(255, 0, 0, 255))))
    private val color2 = setupSetting(config.add(Setting("Color 2", module, Colour(255, 0, 0, 255))))
    private val filledColor = setupSetting(config.add(Setting("Filled Color", module, Colour(255, 0, 0, 255))))
    private val outlineColor = setupSetting(config.add(Setting("Outline Color", module, Colour(255, 0, 0, 255))))
    private val customAlpha = setupSetting(config.add(Setting("Custom Alpha", module, true)))
    private val filled = setupSetting(config.add(Setting("Filled", module, false)))
    private val rainbowFilled = setupSetting(config.add(Setting("Rainbow Filled", module, false)))
    private val rainbowAlpha = setupSetting(config.add(Setting("Rainbow Alpha")))
    private val circle = setupSetting(config.add(Setting("Circle", module, false)))
    private val circleRadius = setupSetting(config.add(Setting("Circle Radius", module, 2.0, 0.1, 10.0, false)))
    private val glow = setupSetting(config.add(Setting("Glow", module, false)))

    //glow radius is just radius
    private val outline = setupSetting(config.add(Setting("Outline", module, false)))
    private val fadeOutline = setupSetting(config.add(Setting("Fade Outline", module, false)))

    private val uniforms = {
        val framebufferShader = mode.valEnum.buffer

        framebufferShader.animationSpeed = animationSpeed.valInt

        if (mode.valEnum === Shaders.ITEMGLOW) {
            (framebufferShader as ItemShader).red = getColor().red / 255f
            framebufferShader.green = getColor().green / 255f
            framebufferShader.blue = getColor().blue / 255f
            framebufferShader.radius = radius.valFloat
            framebufferShader.quality = quality.valFloat
            framebufferShader.blur = blur.valBoolean
            framebufferShader.mix = mix.valFloat
            framebufferShader.alpha = 1f
            framebufferShader.useImage = false
        } else if (mode.valEnum === Shaders.GRADIENT) {
            (framebufferShader as GradientOutlineShader).color = getColor()
            framebufferShader.radius = radius.valFloat
            framebufferShader.quality = quality.valFloat
            framebufferShader.gradientAlpha = gradientAlpha.valBoolean
            framebufferShader.alphaOutline = alphaGradient.valInt
            framebufferShader.duplicate = duplicateOutline.valFloat
            framebufferShader.moreGradient = moreGradientOutline.valFloat
            framebufferShader.creepy = creepyOutline.valFloat
            framebufferShader.alpha = alpha.valFloat
            framebufferShader.numOctaves = numOctavesOutline.valInt
            framebufferShader.update(speedOutline.valDouble)
        } else if (mode.valEnum === Shaders.GLOW) {
            (framebufferShader as GlowShader).red = getColor().red / 255f
            framebufferShader.green = getColor().green / 255f
            framebufferShader.blue = getColor().blue / 255f
            framebufferShader.radius = radius.valFloat
            framebufferShader.quality = quality.valFloat
        } else if (mode.valEnum === Shaders.OUTLINE) {
            (framebufferShader as OutlineShader).red = getColor().red / 255f
            framebufferShader.green = getColor().green / 255f
            framebufferShader.blue = getColor().blue / 255f
            framebufferShader.radius = radius.valFloat
            framebufferShader.quality = quality.valFloat
            framebufferShader.rainbowSpeed = rainbowSpeed.valFloat
            framebufferShader.rainbowStrength = rainbowStrength.valFloat
            framebufferShader.saturation = rainbowSaturation.valFloat
        } else if (mode.valEnum === Shaders.Circle) {
            CircleShader.color1 = color1.colour
            CircleShader.color2 = color2.colour
            CircleShader.filledColor = filledColor.colour
            CircleShader.outlineColor = outlineColor.colour
            CircleShader.customAlpha = customAlpha.valBoolean
            CircleShader.rainbow = rainbowFilled.valBoolean
            CircleShader.circle = circle.valBoolean
            CircleShader.filled = filled.valBoolean
            CircleShader.glow = glow.valBoolean
            CircleShader.outline = outline.valBoolean
            CircleShader.fadeOutline = fadeOutline.valBoolean
            CircleShader.mix = mix.valFloat
            CircleShader.rainbowAlpha = rainbowAlpha.valFloat
            CircleShader.circleRadius = circleRadius.valFloat
            CircleShader.glowRadius = radius.valFloat
            CircleShader.outlineRadius = radius.valFloat
            CircleShader.quality = quality.valFloat
        }
    }

    override fun preInit() : ShaderPattern {
        if(group != null) {
            group!!.add(mode)
            group!!.add(config)
        }

        return this
    }

    override fun init() : ShaderPattern {
        module.register(mode)
        module.register(config)

        module.register(animationSpeed)

        module.register(blur)
        module.register(radius)
        module.register(mix)
        module.register(red)
        module.register(green)
        module.register(blue)
        module.register(rainbow)
        module.register(delay)
        module.register(saturation)
        module.register(brightness)

        module.register(quality)
        module.register(gradientAlpha)
        module.register(alphaGradient)
        module.register(duplicateOutline)
        module.register(moreGradientOutline)
        module.register(creepyOutline)
        module.register(alpha)
        module.register(numOctavesOutline)
        module.register(speedOutline)

        module.register(rainbowSpeed)
        module.register(rainbowStrength)
        module.register(rainbowSaturation)

        module.register(color1)
        module.register(color2)
        module.register(filledColor)
        module.register(outlineColor)
        module.register(customAlpha)
        module.register(filled)
        module.register(rainbowFilled)

        module.register(rainbowAlpha)
        module.register(circle)
        module.register(circleRadius)
        module.register(glow)

        module.register(outline)
        module.register(fadeOutline)

        return this
    }

    fun start(
        ticks : Float
    ) {
        startShader(mode.valEnum, uniforms, ticks)
    }

    fun start() {
        startShader(mode.valEnum, uniforms)
    }

    fun end() {
        endShader(mode.valEnum)
    }

    fun getColor() : Color = if (rainbow.valBoolean) {
        ColorUtils.rainbowRGB(
            delay.valInt,
            saturation.valFloat,
            brightness.valFloat
        )
    } else {
        Color(red.valFloat, green.valFloat, blue.valFloat)
    }
}