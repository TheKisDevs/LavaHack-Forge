package com.kisman.cc.util.world

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.getBlockStateSafe
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.max

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

fun placeable(
    block : Block
) : Boolean = block == Blocks.AIR || block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.LAVA || block == Blocks.FLOWING_LAVA

fun placeable(
    pos : BlockPos
) : Boolean = placeable(getBlockStateSafe(pos).block) && pos.y >= 0 && pos.y <= 255

fun raytrace(
    pos : BlockPos,
    state : Boolean,
    facing : EnumFacing
) : EnumFacing = if(state) {
    BlockUtil2.raytraceFacing(pos)
} else {
    facing
}

fun damageByCrystal(
    target : Entity,
    terrain : Boolean,
    crystal : BlockPos,
    interpolation : Int = 0
) : Float = if(mc.world == null) {
    0f
} else {
     CrystalUtils.calculateDamage(mc.world, crystal.x + 0.5f, crystal.y + 1, crystal.z + 0.5, target, terrain, interpolation, true)
}

fun damageByCrystal(
    terrain : Boolean,
    crystal : BlockPos,
    interpolation : Int = 0
) : Float = damageByCrystal(mc.player, terrain, crystal, interpolation)

fun damageByAnchor(
    target : Entity,
    terrain : Boolean,
    anchor : BlockPos,
    interpolation : Int = 0
) : Float = max(0f, damageByCrystal(target, terrain, anchor, interpolation) - 1f)

fun damageByAnchor(
    terrain : Boolean,
    anchor : BlockPos,
    interpolation : Int = 0
) : Float = damageByAnchor(mc.player, terrain, anchor, interpolation)

fun sphere(
    radius : Float
) : List<BlockPos> = CrystalUtils.getSphere(radius, true, false)