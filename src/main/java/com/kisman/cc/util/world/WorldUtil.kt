package com.kisman.cc.util.world

import com.kisman.cc.util.Globals.mc
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 20:36 of 25.07.2022
 */

val camera = Frustum()

fun isEntityVisible(entity : Entity) : Boolean = isBBVisible(entity.boundingBox)


fun isBBVisible(bb : AxisAlignedBB) : Boolean {
    updateCamera()
    return camera.isBoundingBoxInFrustum(bb)
}

fun updateCamera() {
    updateCamera(mc.renderViewEntity!!)
}

fun updateCamera(entity : Entity) {
    updateCamera(camera, entity)
}

fun updateCamera(camera : ICamera, entity : Entity) {
    camera.setPosition(entity.posX, entity.posY, entity.posZ)
}

fun entityPosition(entity : Entity) : BlockPos = BlockPos(entity.posX, entity.posY, entity.posZ)

fun playerPosition() : BlockPos = entityPosition(mc.player)