package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.CityESP
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.RenderUtil
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.HoleUtil
import com.kisman.cc.util.world.HoleUtil.BlockOffset
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import org.lwjgl.input.Keyboard
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * @author _kisman_
 * @since 18:41 of 10.08.2022
 */
class CityBoss : Module(
    "CityBoss",
    "Breaks surround of nearest player.",
    Category.COMBAT
) {
    private val range = register(Setting("Range", this, 20.0, 1.0, 30.0, true))
    private val down = register(Setting("Down", this, 1.0, 0.0, 3.0, true))
    private val sides = register(Setting("Sides", this, 1.0, 0.0, 4.0, true))
    private val depth = register(Setting("Depth", this, 3.0, 0.0, 10.0, true))
    private val minDMG = register(Setting("Min DMG", this, 10.0, 0.0, 20.0, true))
    private val maxSelfDMG = register(Setting("Max Self DMG", this, 7.0, 0.0, 20.0, true))
    private val ignoreCrystals = register(Setting("Ignore Crystals", this, true))
    private val mineMode = register(Setting("Mine Mode", this, MineMode.Packet))
    private val selectMode = register(Setting("Select Mode", this, SelectMode.Closest))

    private val damages = register(SettingGroup(Setting("Damages", this)))

    private val threads = MultiThreaddableModulePattern(this).init()
    private val targets = TargetFinder(Supplier { range.valDouble }, threads)

    private val cityable = HashMap<EntityPlayer, List<BlockPos>>()
    private var packetMined = false
    private var coordsPacketMined = BlockPos(-1, -1, -1)

    private fun doCityBoss() {
        if (mc.player == null && mc.world == null) return

        cityable.clear()

        val players = mc.world.playerEntities.stream()
            .filter { entityPlayer: EntityPlayer -> entityPlayer !== mc.player }
            .filter { entityPlayer: EntityPlayer -> entityPlayer.getDistanceSq(mc.player) <= range.valDouble * range.valDouble }
            .filter { entityPlayer: EntityPlayer? ->
                !EntityUtil.basicChecksEntity(
                    entityPlayer
                )
            }.collect(Collectors.toList())

        for (player in players) {
            var blocks = EntityUtil.getBlocksIn(player)
            if (blocks.size == 0) continue
            var minY = Int.MAX_VALUE
            for (block in blocks) {
                val y = block.y
                if (y < minY) {
                    minY = y
                }
            }
            if (player.posY % 1 > .2) {
                minY++
            }
            val finalMinY = minY
            blocks = blocks.stream().filter {
                    blockPos: BlockPos -> blockPos.y == finalMinY
            }.collect(Collectors.toList())
            val any = blocks.stream().findAny()
            if (!any.isPresent) {
                continue
            }
            val holeInfo = HoleUtil.isHole(any.get(), false, true)
            if (holeInfo.type == HoleUtil.HoleType.NONE || holeInfo.safety == HoleUtil.BlockSafety.UNBREAKABLE) {
                continue
            }
            val sides = ArrayList<BlockPos>()
            for (block in blocks) {
                sides.addAll(cityableSides(block!!, HoleUtil.getUnsafeSides(block).keys, player))
            }
            if (sides.isNotEmpty()) {
                cityable[player] = sides
            }
        }

        for (poss in cityable.values) {
            var found = false
            for (block in poss) {
                if (mc.player.getDistance(
                        block.x.toDouble(),
                        block.y.toDouble(),
                        block.z.toDouble()
                    ) <= 5
                ) {
                    found = true
                    if (packetMined && coordsPacketMined === block) break
                    if (mc.player.heldItemMainhand.getItem() !== Items.DIAMOND_PICKAXE/* && switchPick.valBoolean*/) {
                        val slot = InventoryUtil.findFirstItemSlot(ItemPickaxe::class.java, 0, 9)
                        if (slot != 1) mc.player.inventory.currentItem = slot
                    }
                    if (mineMode.valString == CityESP.MineMode.Packet.name) {
                        mc.player.swingArm(EnumHand.MAIN_HAND)
                        mc.player.connection.sendPacket(
                            CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                                block,
                                EnumFacing.UP
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayerDigging(
                                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                block,
                                EnumFacing.UP
                            )
                        )
                        packetMined = true
                        coordsPacketMined = block
                    } else {
                        mc.player.swingArm(EnumHand.MAIN_HAND)
                        mc.playerController.onPlayerDamageBlock(block, EnumFacing.UP)
                    }
                    break
                }
            }
            if (found) break
        }
    }


    private fun cityableSides(centre: BlockPos, weakSides: Set<BlockOffset>, player: EntityPlayer): List<BlockPos> {
        val cityableSides: MutableList<BlockPos> = ArrayList()
        val directions = HashMap<BlockPos, BlockOffset>()
        for (weakSide in weakSides) {
            val pos = weakSide.offset(centre)
            if (mc.world.getBlockState(pos).block !== Blocks.AIR) {
                directions[pos] = weakSide
            }
        }
        try {
            directions.forEach(BiConsumer { blockPos: BlockPos, blockOffset: BlockOffset ->
                if (blockOffset == BlockOffset.DOWN) {
                    return@BiConsumer
                }
                val pos1 = blockOffset.left(blockPos.down(down.valInt), sides.valInt)
                val pos2 = blockOffset.forward(blockOffset.right(blockPos, sides.valInt), depth.valInt)
                val square = EntityUtil.getSquare(pos1, pos2)
                val holder = mc.world.getBlockState(blockPos)
                mc.world.setBlockToAir(blockPos)
                for (pos in square) {
                    if (CrystalUtils.canPlaceCrystal(pos.down(), true, ignoreCrystals.valBoolean)) {
                        if (CrystalUtils.calculateDamage(
                                mc.world,
                                pos.x.toDouble() + 0.5,
                                pos.y.toDouble(),
                                pos.z.toDouble() + 0.5,
                                player,
                                false
                            ) >= minDMG.valInt
                        ) {
                            if (CrystalUtils.calculateDamage(
                                    mc.world,
                                    pos.x.toDouble() + 0.5,
                                    pos.y.toDouble(),
                                    pos.z.toDouble() + 0.5,
                                    mc.player,
                                    false
                                ) <= maxSelfDMG.valInt
                            ) {
                                cityableSides.add(blockPos)
                            }
                            break
                        }
                    }
                }
                mc.world.setBlockState(blockPos, holder)
            })
        } catch (ignored : Exception) { }

        return cityableSides
    }

    private fun render(blockPosList: List<BlockPos>) {
        when (selectMode.valString) {
            "Closest" -> {
                blockPosList.stream().min(Comparator.comparing { blockPos: BlockPos ->
                    blockPos.distanceSq(
                        mc.player.posX.toInt().toDouble(),
                        mc.player.posY.toInt().toDouble(),
                        mc.player.posZ.toInt().toDouble()
                    )
                }).ifPresent { blockPos: BlockPos? ->
                    RenderUtil.drawBlockESP(
                        blockPos,
                        0f,
                        1f,
                        0f
                    )
                }
            }
            "All" -> {
                for (blockPos in blockPosList) RenderUtil.drawBlockESP(blockPos, 0f, 1f, 0f)
            }
        }
    }

    enum class MineMode {
        Packet, Vanilla
    }

    enum class SelectMode {
        Closest, All
    }
}