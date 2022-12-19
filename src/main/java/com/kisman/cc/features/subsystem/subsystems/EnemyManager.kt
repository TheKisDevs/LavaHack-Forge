package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.Kisman
import com.kisman.cc.features.subsystem.SubSystem
import com.kisman.cc.util.Globals.mc
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 17:03 of 17.12.2022
 */
object EnemyManager : SubSystem("Enemy Manager") {
    private val enemies = ArrayList<Supplier<EntityPlayer?>>()

    init {
        for(module in Kisman.instance.moduleManager.targetableModules) {
            enemies.add(module.enemySupplier)
        }
    }

    fun enemies() : ArrayList<EntityPlayer> {
        val enemies1 = ArrayList<EntityPlayer>()

        for(enemy in enemies) {
            enemy.get().also { if(it != null) enemies1.add(it) }
        }

        return enemies1
    }

    fun nearestEnemy() : EntityPlayer? {
        var nearestEnemy : EntityPlayer? = null
        var lowestDistance = Double.MAX_VALUE

        for(enemy in enemies()) {
            val currentDistance = mc.player.getDistanceSq(enemy)

            if(nearestEnemy == null || currentDistance < lowestDistance) {
                nearestEnemy = enemy
                lowestDistance = currentDistance
            }
        }

        return nearestEnemy
    }
    fun enemy(
        player : EntityPlayer
    ) : Boolean = enemies().contains(player)
}

annotation class Targetable()
annotation class Target()