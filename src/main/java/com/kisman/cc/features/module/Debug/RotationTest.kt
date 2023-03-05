package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.subsystem.subsystems.nearest
import com.kisman.cc.util.world.rotation

/**
 * @author _kisman_
 * @since 13:01 of 05.03.2023
 */
class RotationTest : Module(
    "RotationTest",
    "Test of new rotation calculation.",
    Category.DEBUG
) {
    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        val entity = nearest()

        if(entity != null) {
            val angles = rotation(entity)

            mc.player.rotationYaw = angles[0]
            mc.player.rotationPitch = angles[1]
        }
    }
}