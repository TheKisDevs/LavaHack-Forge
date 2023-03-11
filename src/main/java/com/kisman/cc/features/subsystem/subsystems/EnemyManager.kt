package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventUpdateEntity
import com.kisman.cc.features.module.combat.AntiBot
import com.kisman.cc.features.subsystem.SubSystem
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.manager.friend.FriendManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 17:03 of 17.12.2022
 */
object EnemyManager : SubSystem("Enemy Manager") {
    private val enemies = ArrayList<Supplier<EntityPlayer?>>()

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

    private var nearestPlayer : EntityPlayer? = null
//    private var nearestEntity : Entity? = null
    private var minDistancePlayer = Double.MAX_VALUE
//    private var minDistanceEntity = Double.MAX_VALUE

    @EventHandler
    private val updateEntity = Listener<EventUpdateEntity>(EventHook {
        if(mc.player != null) {
            val entity = it.entity

            if (entity is EntityPlayer && entity != mc.player && (!AntiBot.instance.isToggled || !AntiBot.instance.mode.checkValString("Zamorozka") || !EntityUtil.antibotCheck(entity)) && !FriendManager.instance.isFriend(entity)) {
                val distance = mc.player.getDistanceSq(entity)

                if (distance < minDistancePlayer) {
                    nearestPlayer = entity
                    minDistancePlayer = distance
                }

                /*if(entity is EntityPlayer) {
                    if(distance < minDistancePlayer) {
                        nearestPlayer = entity
                        minDistancePlayer = distance
                    }
                } else {
                    if(distance < minDistanceEntity) {
                        nearestEntity = entity
                        minDistanceEntity = distance
                    }
                }*/
            }
        }
    })

    @SubscribeEvent
    fun onRenderTick(
        event : TickEvent.RenderTickEvent
    ) {
        if(event.phase == TickEvent.Phase.START) {
            nearestPlayer = null
            minDistancePlayer = Double.MAX_VALUE
        }
    }

    init {
        listeners(updateEntity)

        for(module in Kisman.instance.moduleManager.targetableModules) {
            if(!module::class.java.isAnnotationPresent(TargetsNearest::class.java)) {
                enemies.add(module.enemySupplier)
            }
        }

        enemies.add(Supplier { nearestPlayer })
    }

    fun nearest() : EntityPlayer? = nearestPlayer
//    fun nearestEntity() : Entity? = nearestEntity
}

fun nearest() : EntityPlayer? = EnemyManager.nearest()//For kotlin modules
//fun nearestEntity() : Entity? = EnemyManager.nearestEntity()//For kotlin modules

annotation class Targetable
annotation class Target
annotation class TargetsNearest