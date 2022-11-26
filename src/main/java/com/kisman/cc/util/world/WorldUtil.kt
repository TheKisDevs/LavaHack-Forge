package com.kisman.cc.util.world

import com.kisman.cc.util.Globals.mc
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs

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

fun sendInteractPacket(
    pos : BlockPos
) {
    /*mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(
        pos,
        EnumFacing.UP,
        EnumHand.MAIN_HAND,
        0f, 0f, 0f
    ))*/
    val block = mc.world.getBlockState(pos).block

    if (block !is BlockAir && block !is BlockLiquid) return

    val side = BlockUtil.getFirstFacing(pos) ?: return

    val adjacent = pos.offset(side)
    val opposite = side.opposite

    val vec= Vec3d(adjacent).addVector(0.5, 0.5, 0.5).add(Vec3d(opposite.directionVec).scale(0.5))
//    val adjacentBlock = mc.world.getBlockState(adjacent).block

    var sneaking = false
    if (!mc.player.isSneaking/* && (BLOCK_BLACKLIST.contains(adjacentBlock) || SHULKER_BLOCKS.contains(adjacentBlock))*/) {
        mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING))
        mc.player.isSneaking = true
        sneaking = true
    }

    BlockUtil.rightClickBlock(adjacent, vec, EnumHand.MAIN_HAND, opposite, true)
    mc.player.swingArm(EnumHand.MAIN_HAND)
    mc.rightClickDelayTimer = 4

    if (sneaking) {
        mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
        mc.player.isSneaking = false
    }
}

fun entityBoxBorderLength(
    player : EntityPlayer
) : Double = abs(player.boundingBox.maxX - player.boundingBox.minY)

fun playerBoxBorderLength() : Double = entityBoxBorderLength(mc.player)