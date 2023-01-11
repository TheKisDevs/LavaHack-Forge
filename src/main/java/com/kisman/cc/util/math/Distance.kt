package com.kisman.cc.util.math

import kotlin.math.pow
import kotlin.math.sqrt

fun distance(
    x1 : Int,
    y1 : Int,
    z1 : Int,
    x2 : Int,
    y2 : Int,
    z2 : Int
) : Double {
    return sqrt2(distanceSq(x1, y1, z1, x2, y2, z2))
}

fun distance(
    x1 : Double,
    y1 : Double,
    z1 : Double,
    x2 : Double,
    y2 : Double,
    z2 : Double
) : Double {
    return sqrt2(distanceSq(x1, y1, z1, x2, y2, z2))
}

fun distanceSq(
    x1 : Double,
    y1 : Double,
    z1 : Double,
    x2 : Double,
    y2 : Double,
    z2 : Double
) : Double = (x2 - x1).pow(2) + (y2 - y1).pow(2) + (z2 - z1).pow(2)

fun distanceSq(
    x1 : Int,
    y1 : Int,
    z1 : Int,
    x2 : Int,
    y2 : Int,
    z2 : Int
) : Double = (x2 - x1).toDouble().pow(2) + (y2 - y1).toDouble().pow(2) + (z2 - z1).toDouble().pow(2)