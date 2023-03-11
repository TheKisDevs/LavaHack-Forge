package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo

/**
 * @author _kisman_
 * @since 9:24 of 06.03.2023
 */
@ModuleInfo(
    name = "ModuleInfoTest",
    desc = "Test of ModuleInfo annotation",
    debug = true
)
class ModuleInfoTest : Module()