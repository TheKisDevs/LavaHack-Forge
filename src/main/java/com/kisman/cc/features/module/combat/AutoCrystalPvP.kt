package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.autocrystalpvp.PlaceInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.getBlockStateSafe
import com.kisman.cc.util.movement.MovementUtil
import com.kisman.cc.util.movement.active
import com.kisman.cc.util.movement.gotoPos
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.HoleUtil
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import java.util.stream.Collectors

/**
 * @author _kisman_
 * @since 22:19 of 12.08.2022
 */
@Beta
object AutoCrystalPvP : Module(
    "AutoCrystalPvP",
    "crystal pvp go brr",
    Category.COMBAT
) {
    private val terrain = register(Setting("Terrain", this, true))
    private val targetIsUsingMultiPlace = /*register*/(Setting("Target Is Using Multi Place", this, false))
    private val debug1 = register(Setting("Debug 1", this, false))
    private val debug2 = register(Setting("Debug 2", this, false))
    private val moveStateLogic = register(Setting("Move State Logic", this, MoveStateLogic.MovementUtil))
    private val targetRange = register(Setting("Range", this, 50.0, 1.0, 100.0, true))

    private val autoXPGroup = register(SettingGroup(Setting("Auto XP", this)))
    private val autoXP = register(autoXPGroup.add(Setting("Auto XP", this, false).setTitle("State")))
    private val autoXPSmart = register(autoXPGroup.add(Setting("Auto XP Smart", this, false).setTitle("Smart")))
    private val autoXPMode = SettingEnum("Auto XP Mode", this, AutoXPMode.Vanilla).setTitle("Mode").group(autoXPGroup).register()
    private val autoXPSilentDelay = register(autoXPGroup.add(Setting("Auto Xp Silent Delay", this, 100.0, 0.0, 1000.0, NumberType.TIME).setTitle("Silent Delay")))
    private val autoXPArmorPercent = register(autoXPGroup.add(Setting("Auto XP Armor Percent", this, 50.0, 0.0, 100.0, NumberType.PERCENT).setTitle("Armor Percent")))

    private val autoEatGroup = register(SettingGroup(Setting("Auto Eat", this)))
    private val autoEat = register(autoEatGroup.add(Setting("Auto Eat", this, false).setTitle("State")))

    private val threads = MultiThreaddableModulePattern(this).init()
    private val targets = TargetFinder(targetRange.supplierDouble, threads)

    private val autoXPSilentTimer = TimerUtils()

    private var lastIsMoving = false
    private var isEating = false
    private var autoXPOldSlot = -1

    init {
        super.setDisplayInfo {
            if(targets.target != null) "[${targets.target?.name}]"
            else ""
        }
    }

    override fun onEnable() {
        super.onEnable()
        threads.reset()
        targets.reset()
        autoXPSilentTimer.reset()
        lastIsMoving = false
        autoXPOldSlot = -1
        isEating = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null || mc.player.isDead) {
            return
        }

        targets.update()

        if(debug2.valBoolean) {
            println("Update 1")
        }

        if(targets.target != null) {
            if(debug2.valBoolean) {
                println("Update 2")
            }
            threads.update(Runnable {
                if(debug2.valBoolean) {
                    println("Update 3")
                }
                gotoSafeSpot(targets.target!!)
                autoXP()
                autoEat()
            })
        }
    }

    private fun autoEat() {
        if(autoEat.valBoolean && !isMoving()) {
            var hand = getHandOfItem(Items.GOLDEN_APPLE)

            if(hand == null) {
                val gappleSlot = InventoryUtil.findItem(Items.GOLDEN_APPLE, 0, 9)

                if(gappleSlot == -1) {
                    return
                }

                mc.player.inventory.currentItem = gappleSlot

                hand = getHandOfItem(Items.GOLDEN_APPLE)

                if(hand == null) {
                    return
                }
            }

            handleAutoEat(hand!!)
        } else {
            isEating = false
        }
    }

    private fun handleAutoEat(hand : EnumHand) {
        mc.playerController.processRightClick(mc.player, mc.world, hand)
        isEating = true
    }

    private fun autoXP() {
        if(autoXP.valBoolean && InventoryUtil.isArmorUnderPercent(mc.player, autoXPArmorPercent.valFloat)) {
            if (getAutoXPMode() == AutoXPMode.Vanilla) {
                if (lastIsMoving && !isMoving()) {
                    autoXPOldSlot = mc.player.inventory.currentItem
                    val xpSlot = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9)

                    if (xpSlot == -1) {
                        if (debug1.valBoolean) {
                            ChatUtility.cleanMessage("Initial-AI >> Auto-XP >> No xp in hotbar")
                        }
                    } else {
                        mc.player.inventory.currentItem = xpSlot
                    }
                } else if (!lastIsMoving && !isMoving() && mc.player.heldItemMainhand.item == Items.EXPERIENCE_BOTTLE) {
                    val oldPitch = mc.player.rotationPitch

                    mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))

                    mc.player.rotationPitch = oldPitch
                } else if (!lastIsMoving && isMoving() && autoXPOldSlot != -1) {
                    mc.player.inventory.currentItem = autoXPOldSlot
                }

                autoXPSilentTimer.reset()
            } else {
                if(!isMoving()) {
                    if(autoXPSilentTimer.passedMillis(autoXPSilentDelay.valLong)) {
                        autoXPSilentTimer.reset()

                        val xpSlot = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9)

                        if (xpSlot == -1) {
                            if (debug1.valBoolean) {
                                ChatUtility.cleanMessage("Initial-AI >> Auto-XP >> No xp in hotbar")
                            }
                        } else {
                            val oldSlot = mc.player.inventory.currentItem

                            mc.player.inventory.currentItem = xpSlot

                            val oldPitch = mc.player.rotationPitch

                            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))

                            mc.player.rotationPitch = oldPitch

                            mc.player.inventory.currentItem = oldSlot
                        }
                    }
                }
            }
        }

        lastIsMoving = isMoving()
    }

    private fun getAutoXPMode() : AutoXPMode {
        return if(autoXPSmart.valBoolean) {
            if(isEating) {
                AutoXPMode.Silent
            } else {
                AutoXPMode.Vanilla
            }
        } else {
            autoXPMode.valEnum
        }
    }

    private fun isMoving() : Boolean =
        if(moveStateLogic.valEnum == MoveStateLogic.MovementUtil) MovementUtil.isMoving()
        else active()

    private fun getHandOfItem(item : Item) : EnumHand? =
        if(mc.player.heldItemMainhand.item == item) EnumHand.MAIN_HAND
        else if(mc.player.heldItemOffhand.item == item) EnumHand.OFF_HAND
        else null

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

        if(debug2.valBoolean) {
            println("Spot != null is ${spot != null}")
        }

        if(spot == null) {
            if(debug1.valBoolean) {
                ChatUtility.cleanMessage("Initial-AI >> No safe spot found.")
            }
        } else {
            val pos = spot?.pos
            gotoPos(pos)
            if(debug1.valBoolean) {
                ChatUtility.cleanMessage("Initial-AI >> Going to ${pos.x}, ${pos.y}, ${pos.z}")
            }
        }
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

    private enum class AutoXPMode { Vanilla, Silent }
    private enum class MoveStateLogic { MovementUtil, Baritone }
}