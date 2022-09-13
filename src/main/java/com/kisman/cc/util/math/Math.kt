package com.kisman.cc.util.math

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