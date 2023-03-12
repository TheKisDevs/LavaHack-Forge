package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.*
import com.kisman.cc.features.module.combat.cityboss.Cases
import com.kisman.cc.features.module.combat.cityboss.CrystalBlockPos
import com.kisman.cc.features.subsystem.subsystems.Targetable
import com.kisman.cc.features.subsystem.subsystems.Target
import com.kisman.cc.features.subsystem.subsystems.nearest
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.ObbyPlacementPattern
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.nearestFacing
import com.kisman.cc.util.render.pattern.SlideRendererPattern
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

/**
 * @author _kisman_
 * @since 18:41 of 10.08.2022
 */
@Targetable
@ModuleInfo(
    name = "CityBoss",
    desc = "Breaks surround of nearest player.",
    category = Category.COMBAT,
    wip = true
)
class CityBoss : Module() {
    private val blockRangeCheck = register(Setting("Block Range Check", this, false))
    private val blockRange = register(Setting("Block Range", this, 5.0, 1.0, 6.0, false))
    private val down = register(Setting("Down", this, 1.0, 0.0, 3.0, true))
    private val newVersion = register(Setting("New Version", this, false).setTitle("1.13"))
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
    private val debug3 = register(Setting("Debug 3", this, false))

    private val autorerSync = register(SettingGroup(Setting("Auto ReR Sync", this)))
    private val autorerTargetSync = register(autorerSync.add(Setting("Auto Rer Target Sync", this, false).setTitle("Target")))

    private val renderersGroup = register(SettingGroup(Setting("Renderers", this)))

    private val currentBlockRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Current Block", this))))
    private val currentFacingRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Current Facing", this))))
    private val otherFacingsRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Other Facings", this))))
    private val crystalPosRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Crystal Block", this))))

    private val currentBlockPattern = SlideRenderingRewritePattern(this).prefix("Current Block").group(currentBlockRendererGroup).preInit().init()
    private val currentFacingPattern = RenderingRewritePattern(this).prefix("Current Facing").group(currentFacingRendererGroup).preInit().init()
    private val otherFacingsPattern = RenderingRewritePattern(this).prefix("Other Facings").group(otherFacingsRendererGroup).preInit().init()
    private val crystalPosPattern = SlideRenderingRewritePattern(this).prefix("Crystal Pos").group(crystalPosRendererGroup).preInit().init()

    private val placementGroup = register(SettingGroup(Setting("Base Placement", this)))
    private val placementState = register(placementGroup.add(Setting("Base Placement", this, false).setTitle("State")))
    private val placementPattern = ObbyPlacementPattern(this, true).group(placementGroup).preInit().init()

    private var clicked = false
    private var lastPos : BlockPos? = null

    private val posses = ArrayList<BlockPos>()
    private val currentPosses = ArrayList<BlockPos>()
    private val basePosses = ArrayList<BlockPos>()
    private var processingBlock : BlockPos? = null
    private var baseBlock : BlockPos? = null

    private val currentBlockRenderer = SlideRendererPattern()
    private val crystalPosRenderer = SlideRendererPattern()

    @Target
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
        processingBlock = null
    }

    private fun displayInfo(
        info : String
    ) {
        displayInfo = "[${
            if (player == null) {
                info
            } else {
                "${player!!.name} | $info"
            }
        }]"
    }

    override fun update() {
        if (mc.player == null || mc.world == null) return

        posses.clear()
        currentPosses.clear()
        basePosses.clear()

        processingBlock = null

        player = if(autorerTargetSync.valBoolean) {
            AutoRer.currentTarget
        } else {
            nearest()
        }

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
                displayInfo("Mining burrow")
                mineBlock(entityPosition(player!!))
            } else {
                displayInfo("Mining surround")
                processPlayer(player!!)
            }
        }

        if(placementState.valBoolean && baseBlock != null) {
            placementPattern.placeBlockSwitch(baseBlock!!, Blocks.OBSIDIAN)
        }
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
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
            } catch (_: Exception) {
                //Only by burrow miner
                println("kill yourself <3")
            }
        } else if(!clicked && mineMode.valEnum == MineMode.PacketMine) {
            /*if(!PacketMine.instance.isToggled) {
                PacketMine.instance.isToggled = true
            }*/
//            mc.player.swingArm(EnumHand.MAIN_HAND)
//            mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
            /*mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
            mc.player.swingArm(EnumHand.MAIN_HAND)
            (mc as IMinecraft).invokeSendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown && mc.inGameHasFocus)*/
            mc.playerController.clickBlock(pos, result?.sideHit ?: EnumFacing.UP)
            /*if(PacketMineProvider.position != pos) {
                PacketMineProvider.handleBlockClick(pos, result?.sideHit ?: EnumFacing.UP)
            }*/
            /*mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP)*/
//            PacketMineProvider.posToMine = pos
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

        val eastMax = eastCase?.howManyAirs(EnumFacing.EAST, playerPosition, newVersion.valBoolean) ?: -1
        val westMax = westCase?.howManyAirs(EnumFacing.WEST, playerPosition, newVersion.valBoolean) ?: -1
        val southMax = southCase?.howManyAirs(EnumFacing.SOUTH, playerPosition, newVersion.valBoolean) ?: -1
        val northMax = northCase?.howManyAirs(EnumFacing.NORTH, playerPosition, newVersion.valBoolean) ?: -1

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

        //TODO: rewrite it
        if(down.valInt > 0) {
            posses += playerPosition.down(down.valInt)

            if(mc.world.getBlockState(playerPosition.down()).block != Blocks.AIR) {
                processingBlock = playerPosition.down(down.valInt)
                mineBlock(playerPosition.down(down.valInt))
                return
            }
        }

        run {
            for(pos in currentPosses) {
                val finalPos = playerPosition.add(pos)

                if(pos is CrystalBlockPos) {
                    //TODO: check if finalPos is obby/bedrock ^^^^
                    baseBlock = finalPos
                } else if(mc.world.getBlockState(finalPos).block != Blocks.AIR) {
                    processingBlock = finalPos
                    mineBlock(finalPos)
                    return@run
                }
            }

            processingBlock = null
        }

        for(pos in posses.toArray()) {
            if(pos is CrystalBlockPos) {
                posses.remove(pos)
            }
        }
    }

    private fun processFacing(
        facing : EnumFacing,
        pos : BlockPos
    ) : Cases? = if(newVersion.valBoolean) {
        Cases.SimpleCase1
    } else {
        var airs = Int.MAX_VALUE
        var bestCase : Cases? = null

        for (case in Cases.values()) {
            if (caseSettings[case]!!.valBoolean && (if (down.valInt == 0) case.isIt(
                    facing,
                    pos,
                    newVersion.valBoolean
                ) && (!blockRangeCheck.valBoolean || case.isItInRange(
                    facing,
                    pos,
                    blockRange.valDouble,
                    newVersion.valBoolean
                )) else case.isIt(
                    facing,
                    pos,
                    newVersion.valBoolean,
                    down.valInt
                ) && (!blockRangeCheck.valBoolean || case.isItInRange(
                    facing,
                    pos,
                    blockRange.valDouble,
                    newVersion.valBoolean,
                    down.valInt
                )))
            ) {
                val newAirs = if (down.valInt == 0){
                    case.howManyAirs(facing, pos, newVersion.valBoolean)
                } else {
                    case.howManyAirs(facing, pos, newVersion.valBoolean, down.valInt)
                }

                if (newAirs < airs) {
                    airs = newAirs
                    bestCase = case
                }
            }
        }

        bestCase
    }

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        if(player != null) {
            for(pos in posses) {
                val pos1 = entityPosition(player!!).add(pos)

                if(processingBlock == pos1) {
                    currentBlockRenderer.handleRenderWorld(
                        currentBlockPattern,
                        pos1,
                        null
                    )

                    continue
                }

                if(currentPosses.contains(pos)) {
                    currentFacingPattern.draw(pos1)

                    continue
                }

                otherFacingsPattern.draw(pos1)
            }
        }

        crystalPosRenderer.handleRenderWorld(
            crystalPosPattern,
            baseBlock,
            null
        )
    }

    enum class MineMode {
        Client, PacketMine
    }
}