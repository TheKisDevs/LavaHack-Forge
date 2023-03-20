package com.kisman.cc.features.module.combat.crystalpvphelper

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.combat.HoleFillerRewrite
import com.kisman.cc.features.module.movement.HoleSnap
import com.kisman.cc.features.module.movement.MoveModifier
import com.kisman.cc.features.module.movement.Strafe
import com.kisman.cc.features.subsystem.subsystems.nearest
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.world.distance

/**
 * @author _kisman_
 * @since 10:21 of 19.03.2023
 */
@ModuleInfo(
    name = "MoveOut",
    desc = "Moves you out of hole faster + better :^)",
    submodule = true
)
class MoveOutModule : Module() {
    private val useHoleFiller = register(Setting("Use HoleFiller", this, false))
    private val useStrafe = register(Setting("Use Strafe", this, false))
    private val useStep = register(Setting("Use Step", this, false))
    private val fasterStrafe = register(Setting("Faster Strafe", this, false))
    private val length = register(Setting("Length", this, 1000.0, 100.0, 10000.0, NumberType.TIME))
    private val useHoleSnap = register(Setting("Use HoleSnap", this, false))
    private val playerTrigger = register(Setting("Player Trigger", this, false))
    private val triggerRange = register(Setting("Trigger Range", this, 4.0, 1.0, 10.0, false))

    private val timer = timer()

    private var flag = false

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null || playerTrigger.valBoolean) {
            flag = false
            return
        }

        handleOnEnable()
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            isToggled = false
            return
        }

        if(!flag) {
            if(playerTrigger.valBoolean) {
                val target = nearest()

                if(target != null) {
                    val distance = distance(mc.player.positionVector, target.positionVector)

                    if(distance < triggerRange.valDouble) {
                        handleOnEnable()
                    }
                }
            }

            return
        }

        if(timer.passedMillis(length.valLong)) {
            mc.gameSettings.keyBindBack.pressed = false
            mc.gameSettings.keyBindLeft.pressed = false

            HoleFillerRewrite.instance.isToggled = false
            Strafe.instance.isToggled = false
            MoveModifier.instance!!.step.valBoolean = false

            if(useHoleSnap.valBoolean) {
                HoleSnap.instance.isToggled = true

                isToggled = false
            }
        } else {
            mc.gameSettings.keyBindBack.pressed = true

            if(fasterStrafe.valBoolean) {
                mc.gameSettings.keyBindLeft.pressed = true
            }
        }
    }

    private fun handleOnEnable() {
        if(useHoleFiller.valBoolean) {
            HoleFillerRewrite.instance.isToggled = true
        }

        if(useStrafe.valBoolean) {
            Strafe.instance.isToggled = true
        }

        if(useStep.valBoolean) {
            MoveModifier.instance!!.step.valBoolean = true
        }

        timer.reset()

        flag = true
    }
}