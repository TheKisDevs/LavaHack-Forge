package com.kisman.cc.module.combat.autorer.math

import kotlin.math.pow

class MathUtilKt {
    companion object {
        fun cubic(number: Int): Double {
            return number.toDouble().pow(1 / 3)
        }

        fun cubic(number: Float): Double {
            return number.toDouble().pow(1 / 3)
        }

        fun cubic(number: Double): Double {
            return number.pow(1 / 3)
        }

        fun quart(number: Int): Double {
            return number.toDouble().pow(1 / 4)
        }

        fun quart(number: Float): Double {
            return number.toDouble().pow(1 / 4)
        }

        fun quart(number: Double): Double {
            return number.pow(1 / 4)
        }

        fun ceilToInt(number: Double): Int {
            return kotlin.math.ceil(number).toInt()
        }
    }
}