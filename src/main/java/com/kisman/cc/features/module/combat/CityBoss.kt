package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.features.module.combat.cityboss.Cases
import com.kisman.cc.features.module.exploit.PacketMine
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.providers.PacketMineProvider
import com.kisman.cc.util.render.nearestFacing
import com.kisman.cc.util.world.CrystalUtils
import com.kisman.cc.util.world.HoleUtil
import com.kisman.cc.util.world.HoleUtil.BlockOffset
import com.kisman.cc.util.world.entityPosition
import com.kisman.cc.util.world.playerPosition
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.stream.Collectors
import kotlin.math.min

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
    private val logic = register(Setting("Logic", this, Logic.Skidded))

    private val range = register(Setting("Range", this, 20.0, 1.0, 30.0, true))
    private val blockRangeCheck = register(Setting("Block Range Check", this, false))
    private val blockRange = register(Setting("Block Range", this, 5.0, 1.0, 6.0, false))
    private val down = register(Setting("Down", this, 1.0, 0.0, 3.0, true))
//    private val smartDown = register(Setting("Smart Down", this, false))
    private val sides = register(Setting("Sides", this, 1.0, 0.0, 4.0, true))
    private val depth = register(Setting("Depth", this, 3.0, 0.0, 10.0, true))
    private val ignoreCrystals = register(Setting("Ignore Crystals", this, true))
    private val mineMode = register(Setting("Mine Mode", this, MineMode.Client))
    private val selectMode = register(Setting("Select Mode", this, SelectMode.Closest))

    private val cases = register(SettingGroup(Setting("Cases", this)))
    private val simpleCase1 = register(cases.add(Setting("Simple Case 1", this, true)))
    private val simpleCase2 = register(cases.add(Setting("Simple Case 2", this, true)))
    private val middleCase = register(cases.add(Setting("Middle Case", this, true)))
    private val leftDiagonalCase = register(cases.add(Setting("Left Diagonal", this, true)))
    private val rightDiagonalCase = register(cases.add(Setting("Right Diagonal", this, true)))

    private val damages = register(SettingGroup(Setting("Damages", this)))
    private val minDMG = register(damages.add(Setting("Min DMG", this, 10.0, 0.0, 20.0, true)))
    private val maxSelfDMG = register(damages.add(Setting("Max Self DMG", this, 7.0, 0.0, 20.0, true)))

    private val debug1 = register(Setting("Debug 1", this, false))
    private val debug2 = register(Setting("Debug 2", this, false))

    private val autorerSync = register(SettingGroup(Setting("Auto ReR Sync", this)))
    private val autorerTargetSync = register(autorerSync.add(Setting("Auto Rer Target Sync", this, false).setTitle("Target")))

    private val renderer = RenderingRewritePattern(this).group(register(SettingGroup(Setting("Render", this)))).preInit().init().also {
        it.color1.title = "Other Blocks First"
        it.color2.title = "Other Blocks Second"
    }

    private val currentFacingColor1 = register(renderer.colorGroup.add(Setting("Render Color Other Blocks", this, "Current Facing First", Colour(255, 255, 255, 255))))
    private val currentFacingColor2 = register(renderer.colorGroup.add(Setting("Render Second Color Other Blocks", this, "Current Facing Second", Colour(255, 255, 255, 255))))

    private val processingBlockColor1 = register(renderer.colorGroup.add(Setting("Render Color Processing Block", this, "Processing Block First", Colour(0, 255, 0, 255))))
    private val processingBlockColor2 = register(renderer.colorGroup.add(Setting("Render Second Color Processing Block", this, "Processing Block Second", Colour(0, 255, 0, 255))))

    private val threads = threads()
    private val targets = TargetFinder(Supplier { range.valDouble }, threads)

    private val cityable = HashMap<EntityPlayer, List<BlockPos>>()
    private var sides1 = ArrayList<BlockPos>()
    private var clicked = false
    private var lastPos : BlockPos? = null

    private val posses = ArrayList<BlockPos>()
    private val currentPosses = ArrayList<BlockPos>()
    private var processingBlock : BlockPos? = null

    private var player : EntityPlayer? = null

    private val caseSettings = mapOf<Cases, Setting>(
        Cases.SimpleCase1 to simpleCase1,
        Cases.SimpleCase2 to simpleCase2,
        Cases.MiddleCase to middleCase,
        Cases.LeftDiagonalCase to leftDiagonalCase,
        Cases.RightDiagonalCase to rightDiagonalCase
    )

    override fun onEnable() {
        super.onEnable()
        player = null
        clicked = false
        lastPos = null
    }

    private fun displayInfo(
        info : String
    ) {
        displayInfo = if(player == null) {
            "[no target no fun]"
        } else {
            "[${player!!.name}|$info] "
        }
    }

    override fun update() {
        if (mc.player == null || mc.world == null) return

        posses.clear()
        currentPosses.clear()
        cityable.clear()
        targets.update()

        processingBlock = null

        player = target()

        if(player == null) {
            displayInfo("no target no fun")
            clicked = false
            lastPos = null
            return
        }

        if(canBeBurrowed(player!!) && debug1.valBoolean) {
            // TODO: auto trap action
            displayInfo("Trapping")
        } else {
            if(isBurrowed(player!!) && debug2.valBoolean) {
                displayInfo("Mining burrow block")
                mineBlock(entityPosition(player!!))
            } else {
                displayInfo("Mining surround block")
                processPlayer(player!!)
            }
        }
    }

    private fun target() : EntityPlayer? = if(autorerTargetSync.valBoolean) {
        AutoRer.currentTarget
    } else {
        targets.target
    }

    private fun mineBlock(
        pos : BlockPos?
    ) {
        if(lastPos != pos) {
            clicked = false
        }

        if (pos == null) {//TODO: can block be broken?
            lastPos = null
            return
        }

        if(mineMode.valEnum == MineMode.Client) {
            if(PacketMine.instance.isToggled) {
                PacketMine.instance.isToggled = false
            }

            if (mc.player.heldItemMainhand.getItem() !is ItemPickaxe) {
                val slot = InventoryUtil.findFirstItemSlot(ItemPickaxe::class.java, 0, 9)

                if (slot != 1) {
                    mc.player.inventory.currentItem = slot
                } else {
                    return
                }
            }

            try {
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP)
            } catch (_: Exception) {
                //Only by burrow miner
                println("kill yourself <3")
            }
        } else if(!clicked && mineMode.valEnum == MineMode.PacketMine) {
            if(!PacketMine.instance.isToggled) {
                PacketMine.instance.isToggled = true
            }
            PacketMineProvider.handleBlockClick(pos, null)
            clicked = true
        }

        lastPos = pos
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
    ) : Boolean = mc.world.getBlockState(entityPosition(player).up().up()).block == Blocks.AIR

    private fun isBurrowed(
        player : EntityPlayer
    ) : Boolean = mc.world.getBlockState(player.position).block != Blocks.AIR

    private fun processPlayerNew(
        player : EntityPlayer
    ) {
        val playerPosition = entityPosition(player)

        val eastCase = processFacing(EnumFacing.EAST, playerPosition)
        val westCase = processFacing(EnumFacing.WEST, playerPosition)
        val southCase = processFacing(EnumFacing.SOUTH, playerPosition)
        val northCase = processFacing(EnumFacing.NORTH, playerPosition)

        if(eastCase != null) {
            posses.addAll(if(down.valInt == 0) eastCase.posses(EnumFacing.EAST) else eastCase.down(down.valInt, EnumFacing.EAST))
        }
        if(westCase != null) {
            posses.addAll(if(down.valInt == 0) westCase.posses(EnumFacing.WEST) else westCase.down(down.valInt, EnumFacing.WEST))
        }
        if(southCase != null) {
            posses.addAll(if(down.valInt == 0) southCase.posses(EnumFacing.SOUTH) else southCase.down(down.valInt, EnumFacing.SOUTH))
        }
        if(northCase != null) {
            posses.addAll(if(down.valInt == 0) northCase.posses(EnumFacing.NORTH) else northCase.down(down.valInt, EnumFacing.NORTH))
        }

        if(posses.isEmpty()) {
            return
        }

        val maxAirs = 0

        val eastMax = eastCase?.howManyAirs(EnumFacing.EAST, playerPosition) ?: -1
        val westMax = westCase?.howManyAirs(EnumFacing.WEST, playerPosition) ?: -1
        val southMax = southCase?.howManyAirs(EnumFacing.SOUTH, playerPosition) ?: -1
        val northMax = northCase?.howManyAirs(EnumFacing.NORTH, playerPosition) ?: -1

        var finalCase : Cases? = null
        var finalFacing : EnumFacing? = null

        if(eastMax > maxAirs) {
            finalCase = eastCase
            finalFacing = EnumFacing.EAST
        }
        if(westMax > maxAirs) {
            finalCase = westCase
            finalFacing = EnumFacing.WEST
        }
        if(southMax > maxAirs) {
            finalCase = southCase
            finalFacing = EnumFacing.SOUTH
        }
        if(northMax > maxAirs) {
            finalCase = northCase
            finalFacing = EnumFacing.NORTH
        }

        /*fun smartDownIteration() : Int? {
            if()
        }*/

//        val currentDownValue = -1

        if(finalCase == null) {
            finalCase = Cases.SimpleCase1
            finalFacing = nearestFacing(playerPosition, playerPosition())
        }

        currentPosses.addAll(if(down.valInt == 0) finalCase.posses(finalFacing!!) else finalCase.down(down.valInt, finalFacing!!))

        if(down.valInt > 0) {
            posses += playerPosition.down(down.valInt)

            if(mc.world.getBlockState(playerPosition.down()).block != Blocks.AIR) {
                processingBlock = playerPosition.down(down.valInt)
                mineBlock(playerPosition.down(down.valInt))
                return
            }
        }

        for(pos in (if(down.valInt == 0) finalCase.posses(finalFacing) else finalCase.down(down.valInt, finalFacing))) {
            val finalPos = playerPosition.add(pos)

            if(mc.world.getBlockState(finalPos).block != Blocks.AIR) {
                processingBlock = finalPos
                mineBlock(finalPos)
                return
            }
        }

        processingBlock = null
    }

    private fun processFacing(
        facing : EnumFacing,
        pos : BlockPos
    ) : Cases? {
        var airs = Int.MAX_VALUE
        var bestCase : Cases? = null

        for(case in Cases.values()) {
            if(caseSettings[case]!!.valBoolean && (if(down.valInt == 0) case.isIt(facing, pos) && (!blockRangeCheck.valBoolean || case.isItInRange(facing, pos, blockRange.valDouble)) else case.isIt(facing, pos, down.valInt) && (!blockRangeCheck.valBoolean || case.isItInRange(facing, pos, blockRange.valDouble, down.valInt)))) {
                val newAirs = if(down.valInt == 0) case.howManyAirs(facing, pos) else case.howManyAirs(facing, pos, down.valInt)
                if(newAirs < airs) {
                    airs = newAirs
                    bestCase = case
                }
            }
        }

        return bestCase
    }

    private fun processPlayer(
        player : EntityPlayer
    ) {
        if(logic.valEnum == Logic.Skidded) {
            processPlayerSkidded(player)
        } else if(logic.valEnum == Logic.New) {
            processPlayerNew(player)
        }
    }

    private fun processPlayerSkidded(
        player : EntityPlayer
    ) {
        var blocks = EntityUtil.getBlocksIn(player)

        if (blocks.size == 0) {
            return
        }

        var minY = Int.MAX_VALUE

        for (block in blocks) {
            minY = min(block.y, minY)
        }

        if (player.posY % 1 > .2) {
            minY++
        }

        blocks = blocks.stream().filter { blockPos : BlockPos -> blockPos.y == minY }.collect(Collectors.toList())

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

    @SubscribeEvent
    fun onRenderWorld(event : RenderWorldLastEvent) {
        if(renderer.isActive()) {
            if(logic.valEnum == Logic.Skidded) {
                when (selectMode.valString) {
                    "Closest" -> {
                        sides1.stream().min(Comparator.comparing { pos: BlockPos ->
                            mc.player.getDistanceSq(pos)
                        }).ifPresent {
                            renderer.draw(it)
                        }
                    }
                    "All" -> {
                        for (pos in sides1) {
                            renderer.draw(pos)
                        }
                    }
                }
            } else if(logic.valEnum == Logic.New && player != null) {
                for(pos in posses) {
                    val pos1 = entityPosition(player!!).add(pos)

                    if(processingBlock == pos1) {
                        renderer.draw(
                            pos1,
                            processingBlockColor1.colour,
                            processingBlockColor2.colour
                        )

                        continue
                    }

                    if(currentPosses.contains(pos)) {
                        renderer.draw(
                            pos1,
                            currentFacingColor1.colour,
                            currentFacingColor2.colour
                        )

                        continue
                    }

                    renderer.draw(pos1)
                }
            }
        }
    }

    private enum class Logic { Skidded, New }

    enum class MineMode {
        Client, PacketMine
    }

    enum class SelectMode {
        Closest, All
    }
}