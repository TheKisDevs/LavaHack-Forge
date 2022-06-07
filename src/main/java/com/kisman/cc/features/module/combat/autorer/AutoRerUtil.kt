package com.kisman.cc.features.module.combat.autorer

import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.player.InventoryUtil
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

class AutoRerUtil {
    companion object {
        val mc: Minecraft = Minecraft.getMinecraft()

        fun getDamageByCrystal(target: Entity, terrain: Boolean, crystal: BlockPos): Float {
            if(mc.world == null) {
                return 0f
            }
            return CrystalUtils.calculateDamage(mc.world, crystal.x + 0.5f, crystal.y + 1, crystal.z + 0.5, target, terrain, true)
        }

        fun getSelfDamageByCrystal(terrain: Boolean, crystal: BlockPos): Float {
            return getDamageByCrystal(mc.player, terrain, crystal)
        }

        fun getPlaceInfo(placePos: BlockPos, target: EntityLivingBase, terrain: Boolean): PlaceInfo {
            return PlaceInfo(target, placePos, getSelfDamageByCrystal(terrain, placePos), getDamageByCrystal(target, terrain, placePos), null, null, null)
        }

        fun toVec3dCenter(pos : BlockPos) : Vec3d {
            return Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }

        private fun isPosValid(pos: BlockPos, placeRange : Double, placeWallRange : Double): Boolean {
            return mc.player.getDistance(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5) <= (if (EntityUtil.canSee(pos)) placeRange else placeWallRange)
        }

        fun getPlacePos(
                range : Float,
                wallRange : Float,
                target : EntityPlayer,
                multiPlace : Boolean,
                firePlace : Boolean,
                secondCheck : Boolean,
                thirdCheck : Boolean,
                minDamage : Int,
                maxSelfDamage : Int,
                lethalMult : Float,
                terrain : Boolean,
                armorBreaker : Int
        ) : PlaceInfo? {
            var maxDamage = 0.5f
            var selfDamage_ = 0.0f
            var placePos : BlockPos? = null

            var x : Int = mc.player.position.x - range.toInt()
            while(x.toFloat() <= mc.player.position.x + range) {
                var z : Int = mc.player.position.z - range.toInt()
                while(z.toFloat() <= mc.player.position.z + range) {
                    var y : Int = mc.player.position.y - range.toInt()

                    while(y < mc.player.position.y + range) {
                        val distance =
                                (
                                        (mc.player.position.x - x) *
                                        (mc.player.position.x - x) +
                                        (mc.player.position.z - z) *
                                        (mc.player.position.z - z) +
                                        (mc.player.position.y - y) * (mc.player.position.y - y)
                                ).toDouble()

                        if(distance < range * range) {
                            val pos = BlockPos(x, y, z)
                            if(!thirdCheck || isPosValid(pos, range.toDouble(), wallRange.toDouble())) {
                                if(
                                        CrystalUtils.canPlaceCrystal(
                                                pos,
                                                secondCheck,
                                                true,
                                                multiPlace,
                                                firePlace
                                        )
                                ) {
                                    val targetDamage : Float = getDamageByCrystal(target, terrain, pos)

                                    if(maxDamage <= targetDamage) {

                                        if (targetDamage > minDamage || targetDamage * lethalMult > target.health + target.absorptionAmount || InventoryUtil.isArmorUnderPercent(target, armorBreaker.toFloat())) {
                                            val selfDamage = getSelfDamageByCrystal(terrain, pos)

                                            if (selfDamage <= maxSelfDamage && selfDamage + 2 < mc.player.health + mc.player.absorptionAmount && selfDamage < targetDamage) {
                                                maxDamage = targetDamage
                                                selfDamage_ = selfDamage
                                                placePos = pos
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ++y
                    }
                    ++z
                }
                ++x
            }

            return if(placePos == null) null
            else PlaceInfo(target, placePos, selfDamage_, maxDamage, null, null, null)
        }
    }
}