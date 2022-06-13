package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 17:15 of 04.06.2022
 */
class RotationLock : Module(
    "RotationLock",
    "Locks your yaw and pitch.",
    Category.PLAYER
) {
    private val yaw = register(Setting("Yaw", this, false))
    private val yawValue = register(Setting("Yaw Value", this, 0.0, -180.0, 180.0, true).setVisible { yaw.valBoolean })
    private val pitch = register(Setting("Pitch", this, false))
    private val pitchValue = register(Setting("Pitch Value", this, 0.0, -90.0, 90.0, true).setVisible { pitch.valBoolean })

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(yaw.valBoolean) {
            mc.player.rotationYaw = yawValue.valFloat
        }
        if(pitch.valBoolean) {
            mc.player.rotationPitch = pitchValue.valFloat
        }
    }
}