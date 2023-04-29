package com.kisman.cc.util.client.providers

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup

/**
 * @author _kisman_
 * @since 12:37 of 20.04.2023
 */

fun range(
    module : Module
) = module.register(module.register(SettingGroup(Setting("Range", module))).add(SettingsList("state", Setting("Range Check", module, false).setTitle("State"), "value", Setting("Range", module, 50.0, 0.0, 100.0, true))))