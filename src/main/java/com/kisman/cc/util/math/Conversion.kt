package com.kisman.cc.util.math

import net.minecraft.util.math.MathHelper

val NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000))
val NUM_Z_BITS = NUM_X_BITS
val NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS
val Y_SHIFT = 0 + NUM_Z_BITS
val X_SHIFT = Y_SHIFT + NUM_Y_BITS
val X_MASK = (1L shl NUM_X_BITS) - 1L
val Y_MASK = (1L shl NUM_Y_BITS) - 1L
val Z_MASK = (1L shl NUM_Z_BITS) - 1L

inline fun toLong(x: Int, y: Int, z: Int): Long {
    return x.toLong() and X_MASK shl X_SHIFT or
            (y.toLong() and Y_MASK shl Y_SHIFT) or
            (z.toLong() and Z_MASK)
}