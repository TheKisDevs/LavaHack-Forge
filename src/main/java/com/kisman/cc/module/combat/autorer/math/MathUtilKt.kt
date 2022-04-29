package com.kisman.cc.module.combat.autorer.math

class MathUtilKt {
    companion object {
        fun cubic(number: Int): Double {
            return (number * number * number).toDouble()
        }

        fun cubic(number: Float): Double {
            return (number * number * number).toDouble()
        }

        fun cubic(number: Double): Double {
            return number * number * number
//            return number.pow(1 / 3)
        }

        fun quart(number: Int): Double {
            return (number * number).toDouble()
        }

        fun quart(number: Float): Double {
            return (number * number).toDouble()
//            return number.toDouble().pow(1 / 4)
        }

        fun quart(number: Double): Double {
            return number * number
        }

        fun ceilToInt(number: Double): Int {
            return kotlin.math.ceil(number).toInt()
        }
    }
}