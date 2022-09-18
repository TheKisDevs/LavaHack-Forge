package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.movement.speed.SpeedModes
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 21:50 of 24.06.2022
 */
object SpeedRewrite : Module(
    "SpeedRewrite",
    "Logic-rewrite version of Speed.",
    Category.MOVEMENT
) {
    private val mode = register(Setting("Mode", this, SpeedModes.Strafe))

    val useTimer = register(Setting("Use Timer", this, false))
    val motionXmodifier: Setting = register(Setting("Motion X Modifier", this, 0.0, 0.0, 0.5, false))
    val motionZmodifier: Setting = register(Setting("Motion Z Modifier", this, 0.0, 0.0, 0.5, false))

    val strafeSpeed: Setting = register(Setting("Strafe Speed", this, 0.2873, 0.1, 1.0, false))
    val slow = register(Setting("Slow", this, false))
    val cap: Setting = register(Setting("Cap", this, 10.0, 0.0, 10.0, false))
    val scaleCap = register(Setting("Scale Cap", this, false))
    val lagTime: Setting = register(Setting("Lag Time", this, 500.0, 0.0, 1000.0, NumberType.TIME))

    val useMotion = register(Setting("Use Motion", this, false))
    val useMotionInAir = register(Setting("Use Motion In Air", this, false))
    val jumpMovementFactorSpeed: Setting = register(Setting("Jump Movement Factor Speed", this, 0.265, 0.01, 10.0, false))
    val jumpMovementFactor = register(Setting("Jump Movement Factor", this, false))
    val boostSpeed: Setting = register(Setting("Boost Speed", this, 0.265, 0.01, 10.0, false))
    val boostFactor = register(Setting("Boost Factor", this, false))

//    private val strict = register(Setting("Strict", this, false))

    override fun onEnable() {
        super.onEnable()
        if(mc.player == null || mc.world == null) {
            return
        }
        (mode.valEnum as SpeedModes).mode.onEnable()
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }
        (mode.valEnum as SpeedModes).mode.update()
    }
}