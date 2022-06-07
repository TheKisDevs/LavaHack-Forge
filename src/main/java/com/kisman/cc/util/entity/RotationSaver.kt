package com.kisman.cc.util.entity

import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase

/**
 * @author _kisman_
 * @since 12:41 of 06.06.2022
 */
class RotationSaver(
    val entity : Entity
) {
    var rotationYaw = 0f
    var rotationPitch = 0f
    var rotationYawHead = 0f
    var renderYawOffset = 0f

    constructor() : this(Minecraft.getMinecraft().player)

    fun save() : RotationSaver {
        rotationYaw = entity.rotationYaw
        rotationPitch = entity.rotationPitch
        rotationYawHead = entity.rotationYawHead
        renderYawOffset = (if(entity is EntityLivingBase) entity.renderYawOffset else entity.rotationYaw)
        return this
    }

}