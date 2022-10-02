package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.features.module.render.CityESP
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.RenderUtil
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.HoleUtil
import com.kisman.cc.util.world.HoleUtil.BlockOffset
import com.kisman.cc.util.world.playerPosition
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * @author _kisman_
 * @since 18:41 of 10.08.2022
 */
@Beta
@WorkInProgress
class CityBoss : Module(
    "CityBoss",
    "Breaks surround of nearest player.",
    Category.COMBAT
) {
    private val render = register(Setting("Render", this, true))

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

    private val threads = threads()
    private val targets = TargetFinder(Supplier { range.valDouble }, threads)

    private val cityable = HashMap<EntityPlayer, List<BlockPos>>()
    private var sides1 = ArrayList<BlockPos>()
    private var packetMined = false
    private var coordsPacketMined = BlockPos(-1, -1, -1)

    override fun update() {
        if (mc.player == null || mc.world == null || AutoRer.currentTarget == null) return

        cityable.clear()

        val player = AutoRer.currentTarget

//        if(canBeBurrowed(player)) {
            //TODO: auto trap action
//            println("*trapping*")
//        } else {
            if(isBurrowed(player)) {
                println("*mining burrow block*")
                mineBlock(playerPosition())
            } else {
                println("*mining surround block*")
                processPlayer(player)
            }
//        }
    }

    private fun mineBlock(
        pos : BlockPos
    ) {
        if (packetMined && coordsPacketMined == pos) {
            return
        }

        if (mc.player.heldItemMainhand.getItem() !is ItemPickaxe/* && switchPick.valBoolean*/) {
            val slot = InventoryUtil.findFirstItemSlot(ItemPickaxe::class.java, 0, 9)

            if (slot != 1) {
                mc.player.inventory.currentItem = slot
            } else {
                return
            }
        }

        if (mineMode.valString == CityESP.MineMode.Packet.name) {
            mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    pos,
                    EnumFacing.UP
                )
            )
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    pos,
                    EnumFacing.UP
                )
            )
            packetMined = true
            coordsPacketMined = pos
        } else {
            try {
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP)
            } catch(_ : Exception) {
                //Only my burrow miner
                println("kill yourself <3")
            }
        }
    }

    private fun processPlayerBlocks(
        posses : List<BlockPos>
    ) : Boolean {
        var found = false

        for (pos in posses) {
            if (mc.player.getDistance(
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble()
                ) <= 5
            ) {
                found = true

                mineBlock(pos)

                break
            }
        }

        return found
    }

    private fun canBeBurrowed(
        player : EntityPlayer
    ) : Boolean = mc.world.getBlockState(player.position.up().up()).block == Blocks.AIR

    private fun isBurrowed(
        player : EntityPlayer
    ) : Boolean = mc.world.getBlockState(player.position).block != Blocks.AIR

    private fun processPlayer(
        player : EntityPlayer
    ) {
        var blocks = EntityUtil.getBlocksIn(player)

        if (blocks.size == 0) {
            return
        }

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

        blocks = blocks.stream().filter { blockPos : BlockPos -> blockPos.y == finalMinY }.collect(Collectors.toList())

        val any = blocks.stream().findAny()

        if (!any.isPresent) {
            return
        }

        val holeInfo = HoleUtil.isHole(any.get(), false, true)

        if (holeInfo.type == HoleUtil.HoleType.NONE || holeInfo.safety == HoleUtil.BlockSafety.UNBREAKABLE) {
            return
        }

        val sides = ArrayList<BlockPos>()

        for (block in blocks) {
            sides.addAll(cityableSides(block!!, HoleUtil.getUnsafeSides(block).keys, player))
        }

        if (sides.isNotEmpty()) {
            println("1")
            sides1 = sides
            processPlayerBlocks(sides)
        }
    }

    private fun cityableSides(
        centre : BlockPos,
        weakSides : Set<BlockOffset>,
        player : EntityPlayer
    ) : List<BlockPos> {
        val cityableSides = mutableListOf<BlockPos>()
        val directions = HashMap<BlockPos, BlockOffset>()

        for (weakSide in weakSides) {
            val pos = weakSide.offset(centre)

            if (mc.world.getBlockState(pos).block != Blocks.AIR) {
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

//    @SubscribeEvent
    private fun onRenderWorld(event : RenderWorldLastEvent) {
        if(render.valBoolean) {
            when (selectMode.valString) {
                "Closest" -> {
                    sides1.stream().min(Comparator.comparing { pos : BlockPos ->
                        mc.player.getDistanceSq(pos)
                    }).ifPresent {
                        RenderUtil.drawBlockESP(
                            it,
                            0f,
                            1f,
                            0f
                        )
                    }
                }

                "All" -> {
                    for (pos in sides1) {
                        RenderUtil.drawBlockESP(pos, 0f, 1f, 0f)
                    }
                }
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