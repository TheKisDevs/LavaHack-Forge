package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.client.interfaces.validatorAir
import com.kisman.cc.util.world.playerPosition
import net.minecraft.client.settings.KeyBinding

/**
 * @author _kisman_
 * @since 22:41 of 25.02.2023
 */
class SkyBlockFarmer : Module(
    "SkyBlockFarmer",
    "Only for hypixel skyblock!",
    Category.MOVEMENT
) {
    private val direction = register(Setting("Direction", this, "Left", listOf("Left", "Right")))
    private val shift = register(Setting("Shift", this, false))
    private val mine = register(Setting("Mine", this, false))

    private var key0 = mc.gameSettings.keyBindLeft
    private var prevForward = false

    override fun onEnable() {
        super.onEnable()

        key0 = key(direction.valString == "Left")
        prevForward = false
    }

    override fun onDisable() {
        super.onDisable()

        mc.gameSettings.keyBindForward.pressed = false

        unpress()
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        val pos = playerPosition()
        val current = mc.player.horizontalFacing
        val forward = validatorAir.valid(pos.offset(current))
        val left = validatorAir.valid(pos.offset(current.rotateY().opposite))
        val right = validatorAir.valid(pos.offset(current.rotateY()))

        mc.gameSettings.keyBindForward.pressed = forward
        mc.gameSettings.keyBindSneak.pressed = !forward && shift.valBoolean
        mc.gameSettings.keyBindAttack.pressed = !forward && mine.valBoolean

        if(!forward) {
            if(prevForward) {
                key0 = key(left)
            }

            key0.pressed = left || right
            key(key0 != mc.gameSettings.keyBindLeft).pressed = !(left || right)
        } else {
            unpress()
        }

        prevForward = forward
    }

    private fun unpress() {
        mc.gameSettings.keyBindLeft.pressed = false
        mc.gameSettings.keyBindRight.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        mc.gameSettings.keyBindAttack.pressed = false
    }

    private fun key(
        left : Boolean
    ) : KeyBinding = if(left) {
        mc.gameSettings.keyBindLeft
    } else {
        mc.gameSettings.keyBindRight
    }
}