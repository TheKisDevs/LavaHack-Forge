package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.enums.RenderingRewriteModes
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate", "unused", "HasPlatformType")
class RenderingRewritePattern(
    module : Module
) : AbstractPattern<RenderingRewritePattern>(
    module
) {
    val mode = setupSetting(Setting("Render Mode", module, RenderingRewriteModes.None).setTitle("Mode"))
    val abyss = setupSetting(Setting("Abyss", module, false))
    val lineWidth = setupSetting(Setting("Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible { mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient }.setTitle("Width"))

    val rainbowGroup = setupGroup(SettingGroup(Setting("Rainbow", module)))
    val rainbow = setupSetting(rainbowGroup.add(Setting("Rainbow", module, false)))
    val rainbowSpeed = setupSetting(rainbowGroup.add(Setting("Rainbow Speed", module, 1.0, 0.25, 5.0, false).setVisible(rainbow).setTitle("Speed")))
    val rainbowSat = setupSetting(rainbowGroup.add(Setting("Saturation", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Sat")))
    val rainbowBright = setupSetting(rainbowGroup.add(Setting("Brightness", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Bright")))
    val rainbowGlow = setupSetting(rainbowGroup.add(Setting("Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible(rainbow).setTitle("Glow")))

    //Colors
    val colorGroup = setupGroup(SettingGroup(Setting("Colors", module)))
    val color1 = setupSetting(colorGroup.add(Setting("Render Color", module, "First", Colour(255, 0, 0, 255))))
    val color2 = setupSetting(colorGroup.add(Setting("Render Second Color", module, "Second", Colour(0, 120, 255, 255)).setVisible {
        mode.valEnum == RenderingRewriteModes.FilledGradient ||
        mode.valEnum == RenderingRewriteModes.OutlineGradient ||
        mode.valEnum == RenderingRewriteModes.BothGradient ||
        mode.valEnum == RenderingRewriteModes.GlowOutline ||
        mode.valEnum == RenderingRewriteModes.Glow
    }))

    var alphaSubtract : Double = 0.0

    override fun preInit() : RenderingRewritePattern {
        if(group != null) {
            group!!.add(mode)
            group!!.add(abyss)
            group!!.add(lineWidth)
            group!!.add(rainbowGroup)
            group!!.add(colorGroup)
        }

        return this
    }

    override fun init() : RenderingRewritePattern {
        module.register(mode)
        module.register(abyss)
        module.register(lineWidth)
        module.register(rainbowGroup)
        module.register(rainbow)
        module.register(rainbowSpeed)
        module.register(rainbowSat)
        module.register(rainbowBright)
        module.register(rainbowGlow)
        module.register(colorGroup)
        module.register(color1)
        module.register(color2)

        return this
    }

    fun isActive() : Boolean {
        return mode.valEnum != RenderingRewriteModes.None
    }

    fun draw(
        aabb : AxisAlignedBB,
        color1: Colour,
        color2 : Colour,
        mode : Rendering.Mode?
    ) {
        if(!isActive() || mode == null) {
            return
        }

        var a = aabb
        if(abyss.valBoolean){
            a = AxisAlignedBB(a.minX, a.minY + 1.0, a.minZ, a.maxX, a.maxY + 0.075, a.maxZ)
        }
        if(!rainbowGlow.valString.equals("None")){
            val cAabb = Rendering.correct(a)
            var colour1 = getColor1()
            colour1 = colour1.withAlpha((colour1.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0))
            var colour2 = getColor2()
            colour2 = colour2.withAlpha((colour2.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0))
            var outAlpha1 = 255
            var outAlpha2 = 255
            val reverse = rainbowGlow.valString.equals("ReverseGlow")
            if(reverse){
                outAlpha1 = 0
            } else {
                outAlpha2 = 0
            }
            outAlpha1 = (outAlpha1 - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)
            outAlpha2 = (outAlpha2 - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)
            Rendering.draw(cAabb, lineWidth.valFloat, colour1, colour2, Rendering.Mode.GRADIENT)
            Rendering.draw(cAabb, lineWidth.valFloat, colour1.withAlpha(outAlpha1), colour2.withAlpha(outAlpha2), Rendering.Mode.CUSTOM_OUTLINE)
            return
        }
        Rendering.draw(
            Rendering.correct(a),
            lineWidth.valFloat,
            color1.withAlpha((color1.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)),
            color2.withAlpha((color2.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)),
            mode
        )
    }

    fun draw(
        aabb : AxisAlignedBB,
        color1: Colour,
        color2 : Colour,
        mode : RenderingRewriteModes
    ) {
        draw(
            aabb,
            color1,
            color2,
            mode.mode
        )
    }

    fun draw(aabb : AxisAlignedBB) {
        draw(
            aabb,
            getColor1(),
            getColor2(),
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    fun draw(pos : BlockPos) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            )
        )
    }

    private fun getColor1() : Colour {
        val glow = rainbowGlow.valString
        var alpha = color1.colour.a
        if(glow.equals("ReverseGlow")){
            alpha = 0
        }
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, alpha, rainbowSpeed.valDouble)
        } else {
            color1.colour
        }
    }

    private fun getColor2() : Colour {
        val glow = rainbowGlow.valString
        var alpha = color2.colour.a
        if(glow.equals("Glow")){
            alpha = 0
        }
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, alpha, rainbowSpeed.valDouble)
        } else {
            color2.colour
        }
    }
}