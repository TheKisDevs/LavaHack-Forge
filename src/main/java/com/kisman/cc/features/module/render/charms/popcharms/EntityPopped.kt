package com.kisman.cc.features.module.render.charms.popcharms

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.client.interfaces.IDrawableEntity
import com.kisman.cc.util.entity.EntityCopied
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.world.World

/**
 * @author _kisman_
 * @since 8:44 of 24.03.2023
 */
class EntityPopped(
    world : World,
    profile : GameProfile,
    player : EntityPlayer,
    private val id : Int,
    private val direction : EnumFacing,
    private val speed : Double,
    private val lengths : Triple<Long, Long, Long>,
    private val easings : Triple<EasingEnum.IEasing, EasingEnum.IEasing, EasingEnum.IEasing>
) : IDrawableEntity,
    EntityCopied(
    world,
    profile,
    player
) {
    private val timer = TimerUtils()
    private var removed = false
    private val start = System.currentTimeMillis()

    init {
        timer.reset()

        sync()

        mc.addScheduledTask {
            mc.world.addEntityToWorld(id, this)
        }
    }

    override fun onEntityUpdate() {
        if(timer.passedMillis(lengths.first)) {
            if(!removed) {
                mc.addScheduledTask {
                    mc.world.removeEntityFromWorld(id)
                    removed = true
                }
            }
        } else {
            val coeff = if(direction == EnumFacing.UP) {
                1
            } else {
                -1
            }

            motionY = coeff * speed
            boundingBox = boundingBox.offset(0.0, motionY, 0.0)
            posX = (boundingBox.minX + boundingBox.maxX) / 2.0
            posY = boundingBox.minY
            posZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0
        }
    }

    private fun modify(
        length : Long,
        easing : EasingEnum.IEasing
    ) : Long = (easing.task.doTask((System.currentTimeMillis() - start).toDouble() / length.toDouble()) * length).toLong()

    private fun alpha(
        length : Long,
        easing : EasingEnum.IEasing
    ) = if(timer.passedMillis(length)) {
        val diff = (lengths.first - length).toDouble()

        easing.task.doTask((System.currentTimeMillis() - start).toDouble() / diff).toFloat()
    } else {
        1f
    }

    override fun modelAlpha() = alpha(lengths.second, easings.second)

    override fun wireAlpha() = alpha(lengths.third, easings.third)
}