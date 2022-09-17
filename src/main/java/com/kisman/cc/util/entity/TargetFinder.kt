package com.kisman.cc.util.entity

import com.kisman.cc.features.module.combat.AntiBot
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
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
open class TargetFinder(
    private val range : Supplier<Double>,
    delay : Supplier<Long>,
    threadded : Supplier<Boolean>
) {
    constructor(
        range : Supplier<Double>,
        threads : MultiThreaddableModulePattern
    ) : this(
        range,
        Supplier { threads.delay.valLong },
        Supplier { threads.multiThread.valBoolean }
    )

    protected val mc : Minecraft = Minecraft.getMinecraft()
    var target : EntityPlayer? = null

    private var targetFinderTHandler = ThreadHandler(delay, threadded)

    fun reset() {
        targetFinderTHandler.reset()
    }

    fun update() {
        targetFinderTHandler.update(Runnable {
            mc.addScheduledTask { target = getTarget((range.get() * range.get()).toFloat()) }
        })
    }

    fun getTarget(
        range : Float
    ) : EntityPlayer? {
        return getTarget(
            range,
            range
        )
    }

    open fun getTarget(
        range : Float,
        wallRange : Float
    ) : EntityPlayer? {
        var currentTarget : EntityPlayer? = null
        val size = mc.world.playerEntities.size
        var i = 0
        while (i < size) {
            val player = mc.world.playerEntities[i]
            if (AntiBot.instance.isToggled && AntiBot.instance.mode.checkValString("Zamorozka") && !EntityUtil.antibotCheck(player)) {
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

    protected fun isntValid(
        entity : EntityLivingBase,
        range : Double,
        wallRange : Double
    ) : Boolean {
        return mc.player.getDistanceSq(entity) > (if (mc.player.canEntityBeSeen(entity)) /*(range * range)*/range else /*(wallRange * wallRange)*/wallRange) || entity === mc.player || entity.health <= 0.0f || entity.isDead || FriendManager.instance.isFriend(entity.name)
    }
}