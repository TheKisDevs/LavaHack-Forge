package com.kisman.cc.util.math

import com.kisman.cc.features.module.combat.autorer.math.MathUtilKt
import kotlin.math.sqrt

inline fun distance(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Double {
    return sqrt(distanceSq(x1, y1, z1, x2, y2, z2))
}

inline fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
    return sqrt(distanceSq(x1, y1, z1, x2, y2, z2))
}

inline fun distanceSq(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
    return MathUtilKt.quart(x2 - x1) + MathUtilKt.quart(y2 - y1) + MathUtilKt.quart(z2 - z1)
}

inline fun distanceSq(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Double {
    return MathUtilKt.quart(x2 - x1) + MathUtilKt.quart(y2 - y1) + MathUtilKt.quart(z2 - z1)
}