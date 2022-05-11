package com.kisman.cc.module.movement

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting

class AutoMovement : Module(
        "AutoMovement",
        "AutoWalk + AutoJump",
        Category.MOVEMENT
) {
    val jump = register(Setting("Jump", this, false))
    val walk = register(Setting("Walk", this, false))

    override fun onDisable() {
        if(mc.player == null || mc.world == null) return

        mc.gameSettings.keyBindJump.pressed = false
        mc.gameSettings.keyBindForward.pressed = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null) return

        mc.gameSettings.keyBindJump.pressed = jump.valBoolean
        mc.gameSettings.keyBindForward.pressed = walk.valBoolean
    }
}