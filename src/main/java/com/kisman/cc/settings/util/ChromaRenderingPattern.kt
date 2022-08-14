package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.ChromaColorTypes
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.render.ChromaColorHelper
import com.kisman.cc.util.render.Rendering
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 16:23 of 26.07.2022
 */
class ChromaRenderingPattern(
    val module : Module
) {
    private val gradientMode = Setting("Gradient Mode", module, ChromaColorTypes.Rainbow)
    private val staticColor = Setting("Static Color", module, Colour(255, 255, 255, 255))
    private val pulsiveColor1 = Setting("Pulsive Color 1", module, Colour(255, 255, 255, 255))
    private val pulsiveColor2 = Setting("Pulsive Color 2", module, Colour(255, 255, 255, 255))
    private val diff = Setting("Diff", module, 90.0, 1.0, 150.0, true)
    private val sat = Setting("Sat", module, 100.0, 0.0, 100.0, true)
    private val bright = Setting("Bright", module, 50.0, 0.0, 100.0, true)
    private val speed = Setting("Speed", module, 2.0, 0.1000000000001, 5.0, false)

    fun init() : ChromaRenderingPattern {
        module.register(gradientMode)
        module.register(staticColor)
        module.register(pulsiveColor1)
        module.register(pulsiveColor2)
        module.register(diff)
        module.register(sat)
        module.register(bright)
        module.register(speed)

        return this
    }

    fun drawBlockSide(pos : BlockPos, facing : EnumFacing) {
        val bb = Rendering.correct(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos))

        Rendering.drawChrome(
            bb,
            facing,
            ChromaColorHelper.getColor(
                staticColor.colour,
                arrayOf(pulsiveColor1.colour, pulsiveColor2.colour),
                gradientMode.valEnum as ChromaColorTypes,
                0,
                diff.valInt,
                sat.valFloat,
                bright.valFloat,
                speed.valDouble
            ).color,
            ChromaColorHelper.getColor(
                staticColor.colour,
                arrayOf(pulsiveColor1.colour, pulsiveColor2.colour),
                gradientMode.valEnum as ChromaColorTypes,
                1,
                diff.valInt,
                sat.valFloat,
                bright.valFloat,
                speed.valDouble
            ).color,
            ChromaColorHelper.getColor(
                staticColor.colour,
                arrayOf(pulsiveColor1.colour, pulsiveColor2.colour),
                gradientMode.valEnum as ChromaColorTypes,
                2,
                diff.valInt,
                sat.valFloat,
                bright.valFloat,
                speed.valDouble
            ).color,
            ChromaColorHelper.getColor(
                staticColor.colour,
                arrayOf(pulsiveColor1.colour, pulsiveColor2.colour),
                gradientMode.valEnum as ChromaColorTypes,
                3,
                diff.valInt,
                sat.valFloat,
                bright.valFloat,
                speed.valDouble
            ).color
        )
    }
}