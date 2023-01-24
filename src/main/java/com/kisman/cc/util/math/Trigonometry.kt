package com.kisman.cc.util.math

import com.kisman.cc.util.collections.Pair
import kotlin.math.PI

/**
 * @author _kisman_
 * @since 18:00 of 11.01.2023
 */

private val sinMap = HashMap<Float/*radians*/, Float/*sin*/>()
private val cosMap = HashMap<Float/*radians*/, Float/*cos*/>()
private val tanMap = HashMap<Float/*radians*/, Float/*tan*/>()


private val asinMap = HashMap<Float/*sin*/, Float/*radians*/>()
private val acosMap = HashMap<Float/*cos*/, Float/*radians*/>()
private val atanMap = HashMap<Float/*tan*/, Float/*radians*/>()

private val atan2Map = HashMap<Pair<Float>/*x and y*/, Float/*radians*/>()


private fun processFastFloatFunction(
    map : HashMap<Float, Float>,
    map2 : HashMap<Float, Float>,
    getter : (Float) -> Float,
    `value` : Float,
    inverse : Boolean
) : Float = if(inverse) {
    // asin/acos/atan

    if (map2.containsKey(`value`)) {
        map2[`value`]!!
    } else {
        getter(`value`).also {
            map[it] = `value`
            map2[`value`] = it
        }
    }
} else {
    // sin/cos/tan

    if (map.containsKey(`value`)) {
        map[`value`]!!
    } else {
        getter(`value`).also {
            map[`value`] = it
            map2[it] = `value`
        }
    }
}

fun toDegrees(
    radians : Float
) : Float = radians * 180f / PI.toFloat()

fun toRadians(
    degrees : Float
) : Float = degrees / 180f * PI.toFloat()

fun toDegrees(
    radians : Double
) : Double = radians * 180 / PI

fun toRadians(
    degrees : Double
) : Double = degrees / 180 * PI

fun sin(
    radians : Float
) : Float = processFastFloatFunction(sinMap, asinMap, { kotlin.math.sin(it) }, radians, false)

fun cos(
    radians : Float
) : Float = processFastFloatFunction(cosMap, acosMap, { kotlin.math.cos(it) }, radians, false)

fun tan(
    radians : Float
) : Float = processFastFloatFunction(tanMap, atanMap, { kotlin.math.tan(it) }, radians, false)

fun asin(
    radians : Float
) : Float = processFastFloatFunction(sinMap, asinMap, { kotlin.math.asin(it) }, radians, true)

fun acos(
    radians : Float
) : Float = processFastFloatFunction(cosMap, acosMap, { kotlin.math.acos(it) }, radians, true)

fun atan(
    radians : Float
) : Float = processFastFloatFunction(tanMap, atanMap, { kotlin.math.atan(it) }, radians, true)


fun atan2(
    x : Float,
    y : Float
) : Float = if (atan2Map.containsKey(Pair(x, y))) {
    atan2Map[Pair(x, y)]!!
} else {
    kotlin.math.atan2(x, y).also { atan2Map[Pair(x, y)] = it }
}
