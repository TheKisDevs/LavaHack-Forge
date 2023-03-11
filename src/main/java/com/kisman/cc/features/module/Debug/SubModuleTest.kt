package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Debug.submoduletest.SubModule1
import com.kisman.cc.features.module.Debug.submoduletest.SubModule2
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo

/**
 * @author _kisman_
 * @since 12:36 of 06.03.2023
 */
@ModuleInfo(
    name = "SubModuleTest",
    desc = "Test of submodules",
    debug = true,
    toggled = true,
    toggleable = false,
    modules = [
        SubModule1::class,
        SubModule2::class
    ]
)
class SubModuleTest : Module()