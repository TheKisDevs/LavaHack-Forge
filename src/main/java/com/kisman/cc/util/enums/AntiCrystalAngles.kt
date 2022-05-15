package com.kisman.cc.util.enums

import com.kisman.cc.util.math.vectors.Rotation

/**
 * @author _kisman_
 * @since 15.05.2022
 */
enum class AntiCrystalAngles(
        val rotation: Rotation
) {
    SurroundBlocks(Rotation(Float.NaN, 30f)),
    UppedSurroundBlocks(Rotation(Float.NaN, -80f))
}