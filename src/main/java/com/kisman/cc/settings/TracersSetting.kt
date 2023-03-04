package com.kisman.cc.settings

import com.kisman.cc.util.enums.TracersEntityTypes
import com.kisman.cc.util.enums.TracersSettingTypes

/**
 * @author _kisman_
 * @since 17:06 of 04.03.2023
 */
class TracersSetting(
    val setting : Setting,
    val typeE : TracersEntityTypes,
    val typeS : TracersSettingTypes
)