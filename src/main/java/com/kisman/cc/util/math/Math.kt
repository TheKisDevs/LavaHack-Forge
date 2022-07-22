package com.kisman.cc.util.math

/**
 * @author _kisman_
 * @since 20:33 of 16.07.2022
 */

//Fixed coerceIn method
fun Double.coerceIn(minimumValue: Double, maximumValue: Double): Double {
    if (this < minimumValue) return minimumValue
    if (this > maximumValue) return maximumValue
    return this
}