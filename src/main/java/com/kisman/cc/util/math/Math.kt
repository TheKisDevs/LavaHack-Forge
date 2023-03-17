package com.kisman.cc.util.math

import kotlin.math.sqrt

/**
 * @author _kisman_
 * @since 20:33 of 16.07.2022
 */

//Fixed coerceIn method
fun Double.coerceIn(minimumValue : Double, maximumValue : Double) : Double {
    if (this < minimumValue) return minimumValue
    if (this > maximumValue) return maximumValue
    return this
}

//Fixed coerceIn method
fun Float.coerceIn(minimumValue : Float, maximumValue : Float) : Float {
    if (this < minimumValue) return minimumValue
    if (this > maximumValue) return maximumValue
    return this
}

fun Float.max(second : Float) : Float {
    return kotlin.math.max(this, second)
}

fun Int.max(second : Int) : Int {
    return kotlin.math.max(this, second)
}

fun Double.max(second : Double) : Double {
    return kotlin.math.max(this, second)
}

fun lerp(
    from : Double,
    to : Double,
    delta : Double
) : Double {
    return from + (to - from) * delta
}

fun lerp(
    from : Int,
    to : Int,
    delta : Int
) : Int {
    return from + (to - from) * delta
}

fun Double.min(
    minimumValue : Double
) : Double {
    return kotlin.math.min(
        this,
        minimumValue
    )
}

fun Double.square() : Double = this * this

fun toDelta(start : Long) : Long = System.currentTimeMillis() - start

fun toDelta(start : Long, length : Float) : Float = (toDelta(start).toFloat() / length).coerceIn(0.0f, 1.0f)

fun processFastFunction(
    map : HashMap<Float, Float>,
    getter : (Float) -> Float,
    `value` : Float
) : Float = if(map.containsKey(`value`)) {
    map[`value`]!!
} else {
    getter(`value`).also { map[`value`] = it }
}

private val sqrtMap = HashMap<Float/*squared number*/, Float/*non squared number*/>()

fun sqrt2(
    squared : Float
) : Float = processFastFunction(sqrtMap, { sqrt(it) }, squared)

fun sqrt2(
    squared : Double
) : Double = processFastFunction(sqrtMap, { sqrt(it) }, squared.toFloat()).toDouble()

fun sqrt(
    squared : Float
) : Float = sqrt(squared.toDouble()).toFloat()

fun hypot(
    x : Double,
    y : Double
) : Double = sqrt2(x * x + y * y)