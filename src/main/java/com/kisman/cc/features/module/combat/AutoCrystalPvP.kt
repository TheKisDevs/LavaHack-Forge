package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autocrystalpvp.PlaceInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.getBlockStateSafe
import com.kisman.cc.util.movement.gotoPos
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.HoleUtil
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.stream.Collectors

/**
 * @author _kisman_
 * @since 22:19 of 12.08.2022
 */
object AutoCrystalPvP : Module(
    "AutoCrystalPvP",
    "crystal pvp go brr",
    Category.COMBAT
) {
    private val terrain = register(Setting("Terrain", this, true))
    private val targetIsUsingMultiPlace = register(Setting("Target Is Using Multi Place", this, false))
    private val debug = register(Setting("Debug", this, false))

    private var lastSpot : PlaceInfo? = null

    @SubscribeEvent fun onLivingUpdate(event : LivingEvent.LivingUpdateEvent) {
        if(mc.player == null || mc.world == null || event != mc.player) {
            return
        }

        if(AutoRer.currentTarget != null) {
            gotoSafeSpot(AutoRer.currentTarget)
        }
    }

    private fun findSpot(target : Entity) : PlaceInfo? {
        val list = ArrayList<PlaceInfo>()

        for(pos in CrystalUtils.getSphere(target, 6F, true, false)) {
            if(
                getBlockStateSafe(pos).block == Blocks.AIR
                && getBlockStateSafe(pos.up()).block == Blocks.AIR
                && pos.y >= 1
                && !isBadPos(pos)
            ) {
                list.add(PlaceInfo(
                    target,
                    pos,
                    CrystalUtils.calculateDamage(
                        mc.world,
                        pos.x + 0.5,
                        pos.y.toDouble(),
                        pos.z + 0.5,
                        target,
                        terrain.valBoolean
                    )
                ))
            }
        }

        if(list.isEmpty()) {
            return null
        }

        list.sortWith(Comparator.comparingDouble { it.damage.toDouble() })

        return list[0]
    }

    private fun gotoSafeSpot(target : Entity) {
        val spot = findSpotNew(target)

        if(spot == null) {
            if(debug.valBoolean && lastSpot != null) {
                ChatUtility.cleanMessage("Initial-AI >> No safe spot found.")
            }
        } else {
            val pos = spot?.pos
            gotoPos(pos)
            if(debug.valBoolean) {
                ChatUtility.cleanMessage("Initial-AI >> Going to ${pos.x}, ${pos.y}, ${pos.z}")
            }
        }

        lastSpot = spot
    }

    private fun isBadPos(pos : BlockPos) : Boolean {
        return canPlace(pos.north())
                || canPlace(pos.east())
                || canPlace(pos.west())
                || canPlace(pos.south())
    }

    private fun canPlace(pos : BlockPos) : Boolean {
        return CrystalUtils.canPlaceCrystal(
            pos,
            true,
            true,
            targetIsUsingMultiPlace.valBoolean,
            true
        )
    }

    private fun findSpotNew(target : Entity) : PlaceInfo? {
        val list = ArrayList<PlaceInfo>()

        var maxDamage = 0.5

        for(hole in getHoles(target)) {
            val damage = getMaxTargetDamageFromHole(
                hole,
                6f,
                target
            )

            if(damage > maxDamage) {
                maxDamage = damage.toDouble()
                list.add(PlaceInfo(
                    target,
                    hole,
                    damage
                ))
            }
        }

        if(list.isEmpty()) {
            return null
        }

        list.sortWith(Comparator.comparingDouble { it.damage.toDouble() })

        return list[0]
    }

    private fun getMaxTargetDamageFromHole(
        hole : BlockPos,
        range : Float,
        target : Entity
    ) : Float {
        var maxDamage = 0.5

        for(pos in EntityUtil.getSphere(
            hole,
            range,
            range.toInt() + 1,
            false,
            true,
            0
        )) {
            val damage = CrystalUtils.calculateDamage(
                mc.world,
                pos.x + 0.5,
                pos.y.toDouble(),
                pos.z + 0.5,
                target,
                terrain.valBoolean
            )

            //TODO: damage sync check

            if(damage > maxDamage) {
                //TODO: self damage check, self damage sync check

                maxDamage = damage.toDouble()
            }
        }

        return maxDamage.toFloat()
    }

    private fun getHoles(target : Entity) : ArrayList<BlockPos> {
        val holes = ArrayList<BlockPos>()
        
        for(hole in getPossibleHoles(target, 6f)) {
            val info = HoleUtil.isHole(hole, false, true)

            if(info.type == HoleUtil.HoleType.NONE) continue

            holes.add(hole)
        }

        return holes
    }

    private fun getPossibleHoles(entity : Entity, range : Float) : ArrayList<BlockPos> {
        val possibleHoles = ArrayList<BlockPos>(64)
        var blockPosList = EntityUtil.getSphere(
            entity.position,
            range,
            range.toInt() + 1,
            false,
            true,
            0
        )
        blockPosList = blockPosList.stream().sorted { o1: BlockPos, o2: BlockPos ->
            val a = o1.distanceSq(
                mc.player.posX,
                mc.player.posY,
                mc.player.posZ
            )
            val b = o2.distanceSq(
                mc.player.posX,
                mc.player.posY,
                mc.player.posZ
            )
            a.compareTo(b)
        }.collect(Collectors.toList())
        for (pos in blockPosList) {
            //if (ignoreOwn.getValBoolean() && mc.player.position === pos) continue
            if (mc.world.getBlockState(pos).block != Blocks.AIR) continue
            if (mc.world.getBlockState(pos.add(0, -1, 0)).block == Blocks.AIR) continue
            if (mc.world.getBlockState(pos.add(0, 1, 0)).block != Blocks.AIR) continue
            if (mc.world.getBlockState(pos.add(0, 2, 0)).block == Blocks.AIR) possibleHoles.add(pos)
        }
        return possibleHoles
    }
}