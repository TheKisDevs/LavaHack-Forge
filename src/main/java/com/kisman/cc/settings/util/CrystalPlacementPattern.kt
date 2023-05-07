package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 10:59 of 04.05.2023
 */
class CrystalPlacementPattern(
    module : Module
) : PlacementPattern(
    module,
    true,
    false
) {
    public override fun placeCrystal(
        pos : BlockPos
    ) {
        super.placeCrystal(pos)
    }
}