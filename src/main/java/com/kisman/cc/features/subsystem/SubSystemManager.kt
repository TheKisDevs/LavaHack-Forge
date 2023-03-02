package com.kisman.cc.features.subsystem

import com.kisman.cc.Kisman
import com.kisman.cc.features.subsystem.subsystems.*

/**
 * @author _kisman_
 * @since 20:31 of 09.12.2022
 */
class SubSystemManager {
    var subsystems = listOf(
        EnemyManager,
        HoleProcessor,
        RotationSystem,
        TPSManager
    )

    fun init() {
        for(subsystem in subsystems) {
            Kisman.LOGGER.info("Subsystem Manager: Initializing \"${subsystem.name}\" subsystem!")
            subsystem.init()
        }
    }
}