package com.kisman.cc.util.math.vectors.xyz

import com.kisman.cc.util.Colour
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 17:49 of 24.11.2022
 */
class ColorableSlidePos(
    val pos : BlockPos,
    val colour1 : Colour,
    val colour2 : Colour
) : BlockPos(pos)