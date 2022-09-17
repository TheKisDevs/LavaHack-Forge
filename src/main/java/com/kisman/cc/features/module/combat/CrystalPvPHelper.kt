package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.movement.MovementUtil
import net.minecraft.init.Blocks

/**
 * @author _kisman_
 * @since 5:54 of 15.09.2022
 */
class CrystalPvPHelper : Module(
    "CrystalPvPHelper",
    "Helps with crystal pvp.",
    Category.COMBAT
) {
    private val distance = register(Setting("Distance", this, 3.0, 1.0, 10.0, true))
    private val autoBurrow = register(Setting("Auto Burrow", this, false))
    private val autoSurround = register(Setting("Auto Surround", this, false))
    private val threads = threads()

    private var triggeredRaw = false
    private var lastTriggered = false

    override fun onEnable() {
        threads.reset()
        triggeredRaw = false
        lastTriggered = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        threads.update(Runnable {
            for(player in mc.world.playerEntities) {
                val distanceToPlayer = mc.player.getDistance(player)

                if(distanceToPlayer <= distance.valInt) {
                    triggeredRaw = true
                    break
                }
            }
        })

        val triggered = !lastTriggered && triggeredRaw

        if(!MovementUtil.isMoving() && triggered) {
            if(autoBurrow.valBoolean && (Burrow2.instance.keepOn.valBoolean || mc.world.getBlockState(mc.player.position).block != Blocks.AIR)) {
                Burrow2.instance.enable()
            }

            if(autoSurround.valBoolean) {
                SurroundRewrite.instance.enable()
            }
        }

        lastTriggered = triggeredRaw
    }
}