package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module

/**
 * Useful for 1.16+ servers like mpvp or cc
 *
 * @author _kisman_
 * @since 19:22 of 13.02.2023
 */
class ObbyPlacementPattern(
    module : Module,
    canSwitch : Boolean
) : PlacementPattern(
    module,
    canSwitch,
    false
) {
    init {
        newVersionExcludeAnchors.valBoolean = true
    }
}