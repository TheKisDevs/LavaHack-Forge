package com.kisman.cc.util.world

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.block
import com.kisman.cc.util.math.atan2
import com.kisman.cc.util.math.sqrt
import com.kisman.cc.util.math.sqrt2
import com.kisman.cc.util.math.toDegrees
import com.kisman.cc.util.state
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFire
import net.minecraft.block.BlockLiquid
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.*
import net.minecraft.world.World
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

    val side = BlockUtil2.side(pos) ?: return

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

    BlockUtil2.rightClickBlock(adjacent, vec, EnumHand.MAIN_HAND, opposite, true)
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
) : Boolean = placeable(block(pos)) && pos.y >= 0 && pos.y <= 255

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
    radius : Int
) : List<BlockPos> = sphere(mc.player, radius)

fun sphere(
    entity : Entity,
    radius : Int
) : List<BlockPos> = sphere(BlockPos(entity.posX, entity.posY, entity.posZ), radius)

fun sphere(
    pos : BlockPos,
    radius : Int
) : List<BlockPos> {
    val blocks = mutableListOf<BlockPos>()

    var x = pos.x - radius

    while(x < pos.x + radius) {
        var y = pos.y - radius

        while(y < pos.y + radius) {
            var z = pos.z - radius

            while(z < pos.z + radius) {
                blocks.add(BlockPos(x, y, z))

                z++
            }

            y++
        }

        x++
    }

    return blocks
}

fun rotation(
    pos : BlockPos
) : FloatArray = rotation(mc.player.getPositionEyes(mc.renderPartialTicks), Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5))

fun rotation(
    entity : Entity
) : FloatArray = rotation(mc.player.getPositionEyes(mc.renderPartialTicks), Vec3d(entity.posX, entity.posY + (entity.boundingBox.maxY - entity.boundingBox.minY) / 2, entity.posZ))

fun rotation(
    to : Vec3d
) : FloatArray = rotation(mc.player.getPositionEyes(mc.renderPartialTicks), to)

fun rotation(
    from : Vec3d,
    to : Vec3d
) : FloatArray {
    val deltaX = to.x - from.x
    val deltaY = (to.y - from.y) * -1
    val deltaZ = to.z - from.z
    val distance = sqrt((deltaX * deltaX + deltaZ * deltaZ).toFloat())

    return floatArrayOf(
        MathHelper.wrapDegrees(toDegrees(atan2(deltaZ.toFloat(), deltaX.toFloat())) - 90),
        MathHelper.wrapDegrees(toDegrees(atan2(deltaY.toFloat(), distance)))
    )
}

fun rotate(
    angles : FloatArray
) {
    mc.player.rotationYaw = angles[0]
    mc.player.rotationPitch = angles[1]
}

fun canPlaceCrystal(
    pos : BlockPos,
    check : Boolean,
    entity : Boolean,
    multi : Boolean,
    fire : Boolean,
    newVersion : Boolean,
    newVersionEntities : Boolean
) : Boolean {
    return if(block(pos) == Blocks.BEDROCK || block(pos) == Blocks.OBSIDIAN) {
        if((block(pos.up()) != Blocks.AIR && !(fire && block(pos.up()) == Blocks.FIRE)) || (!newVersion && block(pos.up(2)) != Blocks.AIR)) {
            false
        }

        val upped = pos.up()

        !entity || mc.world.getEntitiesWithinAABB(Entity::class.java, AxisAlignedBB(upped.x.toDouble(), upped.y.toDouble(), upped.z.toDouble(), upped.x + 1.0, upped.y + (if(newVersionEntities) 0.0 else (if(check) 2.0 else 1.0)), upped.z + 1.0), { it !is EntityEnderCrystal || multi }).size == 0
    } else {
        false
    }
}

fun center(
    pos : BlockPos
) = Vec3d(pos).addVector(0.5, 0.5, 0.5)

fun distance(
    vec1 : Vec3d,
    vec2 : Vec3d
) : Double {
    val x = vec1.x - vec2.x
    val y = vec1.y - vec2.y
    val z = vec1.z - vec2.z

    return sqrt2(x * x + y * y + z * z)
}

fun distanceToCenter(
    pos : BlockPos,
    vec : Vec3d
) = distance(center(pos), vec)

fun distanceToCenter(
    entity : Entity,
    pos : BlockPos
) = distance(center(pos), entity.positionVector)

fun flatDistanceToCenter(
    entity : Entity,
    pos : BlockPos
) = distance(center(pos), Vec3d(entity.posX, pos.y + 0.5, entity.posZ))

fun center(
    aabb : AxisAlignedBB
) = Vec3d(
    aabb.minX + (aabb.minX + aabb.maxX) / 2,
    aabb.minY + (aabb.minY + aabb.maxY) / 2,
    aabb.minZ + (aabb.minZ + aabb.maxZ) / 2
)

fun center(
    world : World,
    pos : BlockPos
) : Vec3d {
    val aabb = state(pos).getBoundingBox(world, pos)

    return Vec3d(
        pos.x + (aabb.minX + aabb.maxX) / 2,
        pos.y + (if(block(pos) is BlockFire) 0.0 else (aabb.minY + aabb.maxY)) / 2,
        pos.z + (aabb.minZ + aabb.maxZ) / 2
    )
}

fun feetBlocks(
    entity : Entity
) = mutableSetOf<BlockPos>().also {
    val aabb = entity.boundingBox

    val diffX = (aabb.maxX - aabb.minX) / 2
    val diffZ = (aabb.maxZ - aabb.minZ) / 2

    it.add(BlockPos(entity.posX + diffX, entity.posY, entity.posZ + diffZ))
    it.add(BlockPos(entity.posX + diffX, entity.posY, entity.posZ - diffZ))
    it.add(BlockPos(entity.posX - diffX, entity.posY, entity.posZ + diffZ))
    it.add(BlockPos(entity.posX - diffX, entity.posY, entity.posZ - diffZ))
}

fun dynamicBlocks(
    entity : Entity
) : List<BlockPos> {
    val base = feetBlocks(entity)
    val list = mutableListOf<BlockPos>()

    for(pos in base) {
        pos.north().also { if(!base.contains(it)) list.add(it) }
        pos.south().also { if(!base.contains(it)) list.add(it) }
        pos.west().also { if(!base.contains(it)) list.add(it) }
        pos.east().also { if(!base.contains(it)) list.add(it) }
    }

    return list
}

fun dynamicBlocksSorted(
    entity : Entity
) : Map<EnumFacing?, List<BlockPos?>> {
    val posses = dynamicBlocks(entity)
    val map = mutableMapOf<EnumFacing?, List<BlockPos?>>()
    val entityPosition = entityPosition(entity)

    for(pos in posses) {
        if(pos.x == entityPosition.x || pos.z == entityPosition.z) {
            var pair : BlockPos? = null

            pos.north().also { if(posses.contains(it)) pair = it }
            pos.south().also { if(posses.contains(it)) pair = it }
            pos.west().also { if(posses.contains(it)) pair = it }
            pos.east().also { if(posses.contains(it)) pair = it }

            val diffX = (pos.x - entityPosition.x).coerceIn(-1..1)
            val diffZ = (pos.z - entityPosition.z).coerceIn(-1..1)
            val vec = Vec3i(diffX, 0, diffZ)
            var facing : EnumFacing? = null

            for(facing0 in EnumFacing.values()) {
                if(facing0.directionVec == vec) {
                    facing = facing0

                    break
                }
            }

            map[facing] = listOf(pos, pair)
        }
    }

    return map
}

fun highlight(
    centre : List<BlockPos>
) : Set<BlockPos> {
    val set = mutableSetOf<BlockPos>()

    for(pos in centre) {
        set.add(pos.north())
        set.add(pos.south())
        set.add(pos.west())
        set.add(pos.east())
    }

    set.removeAll(centre.toSet())

    return set
}

fun canCollide(
    pos : BlockPos,
    liquid : Boolean
) : Boolean {
    val state = state(pos)

    return state.block.canCollideCheck(state, liquid)
}

fun canCollide(
    pos : BlockPos
) = canCollide(pos, false)

fun canPlace(
    centre : BlockPos
) : Boolean {
    for(facing in EnumFacing.values()) {
        val pos = centre.offset(facing)

        if(canCollide(pos)) {
            return true
        }
    }

    return false
}

fun helpingBlock(
    centre : BlockPos,
    validator : (BlockPos) -> Boolean
) : BlockPos? {
    for(facing in EnumFacing.values()) {
        val pos = centre.offset(facing)

        if(validator(pos) && canCollide(pos) && canPlace(pos)) {
            return pos
        }
    }

    return null
}

/*
* private boolean canPlaceCrystal(BlockPos pos, boolean check, boolean entity, boolean multiPlace, boolean firePlace, boolean newVerPlace, boolean newVerEntities) {
        if(mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) {
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !(firePlace && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.FIRE))) return false;
            if (!newVerPlace && !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) return false;
            BlockPos boost = pos.add(0, 1, 0);
            return !entity || mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost.getX(), boost.getY(), boost.getZ(), boost.getX() + 1, boost.getY() + (newVerEntities ? 0 : (check ? 2 : 1)), boost.getZ() + 1), e -> !(e instanceof EntityEnderCrystal) || multiPlace).size() == 0;
        }
        return false;
    }*/