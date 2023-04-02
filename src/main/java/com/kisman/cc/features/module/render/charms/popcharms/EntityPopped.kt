package com.kisman.cc.features.module.render.charms.popcharms

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.client.interfaces.IFakeEntity
import com.kisman.cc.util.entity.EntityCopied
import com.mojang.authlib.GameProfile
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
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
    private val length : Long
) : EntityCopied(
    world,
    profile,
    player
) {
    private val timer = TimerUtils()
    private var removed = false

    init {
        timer.reset()

        sync()

        mc.addScheduledTask {
            mc.world.addEntityToWorld(id, this)
        }
    }

    override fun onEntityUpdate() {
        if(timer.passedMillis(length)) {
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
}