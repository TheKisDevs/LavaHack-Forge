package com.kisman.cc.features.aiimprovements

import com.kisman.cc.util.math.atan2
import com.kisman.cc.util.math.distance
import com.kisman.cc.util.math.toDegrees
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.ai.EntityLookHelper
import net.minecraft.util.math.MathHelper

/**
 * @author _kisman_
 * @since 18:58 of 11.01.2023
 */
class ImprovedLookHelper(
    entity : EntityLiving
) : EntityLookHelper(
    entity
) {
    override fun onUpdateLook() {
        entity.rotationPitch = 0f

        if(isLooking) {
            isLooking = false

            val distanceX = posX - entity.posX
            val distanceY = posY - entity.posY - entity.eyeHeight
            val distanceZ = posZ - entity.posZ
            val distance = distance(posX, entity.posX, posY, entity.posY + entity.eyeHeight, posZ, entity.posZ)

            val yaw = toDegrees(atan2(distanceZ.toFloat(), distanceX.toFloat())) - 90f
            val pitch = toDegrees(-atan2(distanceY.toFloat(), distance.toFloat()))

            entity.rotationYawHead = updateRotation(entity.rotationYawHead, yaw, deltaLookYaw)
            entity.rotationPitch = updateRotation(entity.rotationPitch, pitch, deltaLookPitch)
        } else {
            entity.rotationYawHead =updateRotation(entity.rotationYawHead, entity.renderYawOffset, 10f)
        }

        if(!entity.navigator.noPath()) {
            val deltaYaw = MathHelper.wrapDegrees(entity.rotationYawHead - entity.renderYawOffset)

            if(deltaYaw < -75) {
                entity.rotationYawHead = entity.renderYawOffset - 75
            } else if(deltaYaw > 75) {
                entity.rotationYawHead = entity.renderYawOffset + 75
            }
        }
    }
}