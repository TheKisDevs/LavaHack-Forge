package com.kisman.cc.util.entity

import com.kisman.cc.features.module.combat.AntiBot
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.thread.kisman.ThreadHandler
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 22:33 of 22.06.2022
 */
class TargetFinder(
    private val range : Supplier<Double>,
    delay : Supplier<Long>,
    threadded : Supplier<Boolean>
) {
    private val mc: Minecraft = Minecraft.getMinecraft()
    var target : EntityPlayer? = null

    private var targetFinderTHandler = ThreadHandler(delay, threadded)

    fun reset() {
        targetFinderTHandler.reset()
    }

    fun update() {
        targetFinderTHandler.update(Runnable {
            mc.addScheduledTask { target = EntityUtil.getTarget((range.get() * range.get()).toFloat()) }
        })
    }

    fun getTarget(range : Float) : EntityPlayer? {
        return getTarget(range, range)
    }

    fun getTarget(range : Float, wallRange : Float) : EntityPlayer? {
        var currentTarget: EntityPlayer? = null
        val size = mc.world.playerEntities.size
        var i = 0
        while (i < size) {
            val player = mc.world.playerEntities[i]
            if (!EntityUtil.antibotCheck(player) && AntiBot.instance.isToggled && AntiBot.instance.mode.checkValString("Zamorozka")) {
                ++i
                continue
            }
            if (!isntValid(player, range.toDouble(), wallRange.toDouble())) {
                if (currentTarget == null) currentTarget = player else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) currentTarget = player
            }
            ++i
        }
        return currentTarget
    }

    fun isntValid(entity: EntityLivingBase, range: Double, wallRange: Double): Boolean {
        return mc.player.getDistanceSq(entity) > (if (mc.player.canEntityBeSeen(entity)) range else wallRange) || entity === mc.player || entity.health <= 0.0f || entity.isDead || FriendManager.instance.isFriend(entity.name)
    }
}