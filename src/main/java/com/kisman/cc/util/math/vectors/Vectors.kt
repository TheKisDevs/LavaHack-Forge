package com.kisman.cc.util.math.vectors

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.roundToInt

/**
 * @author _kisman_
 * @since 19:25 of 29.12.2022
 */

fun d2i(
    vec : Vec3d
) : Vec3i = Vec3i(vec.x.roundToInt(), vec.y.roundToInt(), vec.z.roundToInt())