package com.kisman.cc.util.world

import com.kisman.cc.util.Globals
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB

/**
 * @author _kisman_
 * @since 20:36 of 25.07.2022
 */
val camera = Frustum()

fun isEntityVisible(entity : Entity) : Boolean {
    return isBBVisible(entity.boundingBox)
}

fun isBBVisible(bb : AxisAlignedBB) : Boolean {
    updateCamera()
    return camera.isBoundingBoxInFrustum(bb)
}

fun updateCamera() {
    updateCamera(Globals.mc.renderViewEntity!!)
}

fun updateCamera(entity : Entity) {
    updateCamera(camera, entity)
}

fun updateCamera(camera : ICamera, entity : Entity) {
    camera.setPosition(entity.posX, entity.posY, entity.posZ)
}