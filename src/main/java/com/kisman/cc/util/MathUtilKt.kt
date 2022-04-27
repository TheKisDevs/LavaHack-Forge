package com.kisman.cc.util

class MathUtilKt {
    companion object {
        @JvmStatic
        fun toDelta(start: Long, length: Int): Float {
            return toDelta(start, length.toFloat())
        }

        @JvmStatic
        fun toDelta(start: Long, length: Long): Float {
            return toDelta(start, length.toFloat())
        }

        @JvmStatic
        fun toDelta(start: Long, length: Float): Float {
            return (toDelta(start).toFloat() / length).coerceIn(0.0f, 1.0f)
        }

        @JvmStatic
        fun toDelta(start: Long): Long {
            return System.currentTimeMillis() - start
        }
    }
}