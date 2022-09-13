package com.kisman.cc.gui.halq.util

private val floatingPointNumber = Regex("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$")

fun parseNumber(
    value : String,
    oldValue : Double
) : Double {
    return if(value.matches(floatingPointNumber)) {
        try {
            java.lang.Double.parseDouble(value)
        } catch(e : Exception) {
            oldValue
        }
    } else {
        oldValue
    }
}