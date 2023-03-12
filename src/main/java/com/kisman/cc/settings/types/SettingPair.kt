package com.kisman.cc.settings.types

import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 16:05 of 12.03.2023
 */
class SettingPair<S1 : Setting, S2 : Setting>(
    @JvmField val first : S1,
    @JvmField val second : S2
)