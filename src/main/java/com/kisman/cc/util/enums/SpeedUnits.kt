package com.kisman.cc.util.enums

import java.text.DecimalFormat

/**
 * @author _kisman_
 * @since 14.05.2022
 */
enum class SpeedUnits(
        val formatter : DecimalFormat,
        val displayInfo : String
) {
    BPS(DecimalFormat("#.#"), "b/s"),
    KMH(DecimalFormat("#.#"), "km/h")
}