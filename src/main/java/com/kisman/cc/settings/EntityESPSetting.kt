package com.kisman.cc.settings

import com.kisman.cc.settings.util.EntityESPRendererPattern
import com.kisman.cc.util.enums.EntityESPTypes

class EntityESPSetting(
        val setting : Setting,
        val typeE : EntityESPTypes,
        val typeS : EntityESPRendererPattern.SettingTypes
)