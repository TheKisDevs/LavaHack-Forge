package com.kisman.cc.util.math.vectors

import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * @author _kisman_
 * @since 19:25 of 29.12.2022
 */

fun d2i(
    vec : Vec3d
) : Vec3i = Vec3i(vec.x.roundToInt(), vec.y.roundToInt(), vec.z.roundToInt())

fun i2d(
    vec : Vec3i
) : Vec3d = Vec3d(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

fun vecToString(
    vec : Vec3d
) : String = StringBuilder().also {
    it.append('(')
    it.append(floor(vec.x).toInt())
    it.append(", ")
    it.append(floor(vec.y).toInt())
    it.append(", ")
    it.append(floor(vec.z).toInt())
    it.append(")")
} .toString()