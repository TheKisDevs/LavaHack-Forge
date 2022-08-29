package com.kisman.cc.features.nocom

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.nocom.modules.NoComMainModule
import com.kisman.cc.features.nocom.modules.NoComTrackerModule

/**
 * @author _kisman_
 * @since 0:38 of 28.08.2022
 */
class NoComModuleManager {
    val modules = ArrayList<Module>()

    init {
        modules.add(NoComMainModule())
        modules.add(NoComTrackerModule())
    }
}