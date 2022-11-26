package com.kisman.cc.util.enums

/**
 * @author _kisman_
 * @since 16:55 of 26.11.2022
 */
enum class BurrowModes(
    val displayName : String
) {
    Normal("Normal"),
    CrystalPvPcc("crystalpvp.cc")

    ;

    override fun toString() : String = displayName
}