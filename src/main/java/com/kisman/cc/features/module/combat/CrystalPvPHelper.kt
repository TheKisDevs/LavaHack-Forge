package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.movement.MovementUtil
import net.minecraft.init.Blocks

/**
 * @author _kisman_
 * @since 5:54 of 15.09.2022
 */
@Beta
@WorkInProgress
class CrystalPvPHelper : Module(
    "CrystalPvPHelper",
    "Helps with crystal pvp.",
    Category.COMBAT
) {
    private val distance = register(Setting("Distance", this, 3.0, 1.0, 10.0, true))
    private val autoBurrow = register(Setting("Auto Burrow", this, false))
    private val autoSurround = register(Setting("Auto Surround", this, false))
    private val threads = threads()

    private var triggered = false

    override fun onEnable() {
        threads.reset()
        triggered = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        threads.update(Runnable {
            if(!MovementUtil.isMoving()) {
                for (player in mc.world.playerEntities) {
                    if (player == mc.player) {
                        continue
                    }

                    val distanceToPlayer = mc.player.getDistance(player)

                    if (distanceToPlayer <= distance.valInt) {
                        if(autoBurrow.valBoolean && (Burrow2.instance.keepOn.valBoolean || mc.world.getBlockState(mc.player.position).block != Blocks.AIR)) {
                            Burrow2.instance.enable()
                        }

                        if(autoSurround.valBoolean) {
                            SurroundRewrite.instance.enable()
                        }

                        triggered = true

                        break
                    }
                }
            }

            triggered = false
        })
    }
}