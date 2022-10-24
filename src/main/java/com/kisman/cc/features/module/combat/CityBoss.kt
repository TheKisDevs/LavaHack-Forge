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
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.nearestFacing
import com.kisman.cc.util.world.entityPosition
import com.kisman.cc.util.world.playerPosition
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.Supplier

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
    private val range = register(Setting("Range", this, 20.0, 1.0, 30.0, true))
    private val blockRangeCheck = register(Setting("Block Range Check", this, false))
    private val blockRange = register(Setting("Block Range", this, 5.0, 1.0, 6.0, false))
    private val down = register(Setting("Down", this, 1.0, 0.0, 3.0, true))
//    private val smartDown = register(Setting("Smart Down", this, false))
    private val mineMode = register(Setting("Mine Mode", this, MineMode.Client))

    private val cases = register(SettingGroup(Setting("Cases", this)))
    private val simpleCase1 = register(cases.add(Setting("Simple Case 1", this, true)))
    private val simpleCase2 = register(cases.add(Setting("Simple Case 2", this, true)))
    private val middleCase = register(cases.add(Setting("Middle Case", this, true)))
    private val leftDiagonalCase = register(cases.add(Setting("Left Diagonal", this, true)))
    private val rightDiagonalCase = register(cases.add(Setting("Right Diagonal", this, true)))

//    private val damages = register(SettingGroup(Setting("Damages", this)))
//    private val minDMG = register(damages.add(Setting("Min DMG", this, 10.0, 0.0, 20.0, true)))
//    private val maxSelfDMG = register(damages.add(Setting("Max Self DMG", this, 7.0, 0.0, 20.0, true)))

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
        displayInfo = "[${
            if (player == null) {
                info
            } else {
                "${player!!.name}|$info"
            }
        }]"
    }

    override fun update() {
        if (mc.player == null || mc.world == null) return

        posses.clear()
        currentPosses.clear()
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

        val result = mc.world.rayTraceBlocks(Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight().toDouble(), mc.player.posZ), Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5))

        if(mineMode.valEnum == MineMode.Client) {
            /*if(PacketMine.instance.isToggled) {
                PacketMine.instance.isToggled = false
            }*/

            if (mc.player.heldItemMainhand.getItem() !is ItemPickaxe) {
                val slot = InventoryUtil.findFirstItemSlot(ItemPickaxe::class.java, 0, 9)

                if (slot != 1) {
                    mc.player.inventory.currentItem = slot
                } else {
                    return
                }
            }

            try {
                mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
                mc.player.swingArm(EnumHand.MAIN_HAND)
            } catch (_: Exception) {
                //Only by burrow miner
                println("kill yourself <3")
            }
        } else if(!clicked && mineMode.valEnum == MineMode.PacketMine) {
            /*if(!PacketMine.instance.isToggled) {
                PacketMine.instance.isToggled = true
            }*/

            /*mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
            mc.player.swingArm(EnumHand.MAIN_HAND)
            (mc as IMinecraft).invokeSendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown && mc.inGameHasFocus)
//            mc.playerController.clickBlock(pos, result?.sideHit ?: EnumFacing.UP)
//            PacketMineProvider.handleBlockClick(pos, result?.sideHit ?: EnumFacing.UP)*/
            mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP)
            clicked = true
        }

        lastPos = pos
    }

    private fun canBeBurrowed(
        player : EntityPlayer
    ) : Boolean = mc.world.getBlockState(entityPosition(player).up().up()).block == Blocks.AIR

    private fun isBurrowed(
        player : EntityPlayer
    ) : Boolean = mc.world.getBlockState(player.position).block != Blocks.AIR

    private fun processPlayer(
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

    @SubscribeEvent
    fun onRenderWorld(event : RenderWorldLastEvent) {
        if(renderer.isActive() && player != null) {
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
    enum class MineMode {
        Client, PacketMine
    }
}