package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import kotlin.math.abs

/**
 * @author _kisman_
 * @since 21:42 of 28.08.2022
 */
class FastFallTest : Module(
    "FastFall",
    "Test of fast fall module uwa?",
    Category.DEBUG
) {
    private val logic = register(Setting("Logic", this, Logic.Motion))
    private val progression = register(Setting("Progression", this, Progression.Arithmetic))
    private val coefficient = register(Setting("Coefficient", this, 1.0, 0.1, 2.0, false))
    private val groundCheck = register(Setting("Ground Check", this, false))

    private var lastPosY = 0.0

    override fun onEnable() {
        super.onEnable()
        lastPosY = 0.0
    }

    override fun update() {
        if(mc.player == null || mc.world == null ) {
            lastPosY = 0.0
            return
        }

        if(groundCheck.valBoolean && mc.player.onGround) {
            lastPosY = mc.player.posY
            return
        }

        if(logic.valEnum == Logic.Motion) {
           if(mc.player.motionY >= 0) {
               return
           }
        } else if(mc.player.posY >= lastPosY) {
            return
        }

        if(progression.valEnum == Progression.Arithmetic) {
            mc.player.motionY += -coefficient.valDouble
        } else {//Geometric
            mc.player.motionY = abs(mc.player.motionY)  * -coefficient.valDouble
        }

        lastPosY = mc.player.posY
    }

    private enum class Logic {
        Position,
        Motion
    }

    private enum class Progression {
        Arithmetic,
        Geometric
    }
}