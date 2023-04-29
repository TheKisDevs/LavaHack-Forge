package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.*
import com.kisman.cc.features.module.combat.cityboss.Case
import com.kisman.cc.features.module.combat.cityboss.Cases
import com.kisman.cc.features.module.combat.cityboss.CrystalBlockPos
import com.kisman.cc.features.module.exploit.PacketMineRewrite3
import com.kisman.cc.features.subsystem.subsystems.Targetable
import com.kisman.cc.features.subsystem.subsystems.Target
import com.kisman.cc.features.subsystem.subsystems.nearest
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.ObbyPlacementPattern
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.block
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.world.dynamicBlocksSorted
import com.kisman.cc.util.world.entityPosition
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
    category = Category.COMBAT
)
class CityBoss : Module() {
    private val blockReachDistanceCheck = register(Setting("Block Reach Check", this, false))
    private val blockReachDistance = register(Setting("Block Reach", this, 5.0, 1.0, 6.0, false))
    private val down = register(Setting("Down", this, 1.0, 0.0, 3.0, true))
    private val newVersion = register(Setting("New Version", this, false).setTitle("1.13"))
//    private val smartDown = register(Setting("Smart Down", this, false))
    private val breaking = register(SettingGroup(Setting("Breaking", this)))
    private val usePacketMine = register(breaking.add(Setting("Use Packet Mine", this, true)))
    private val allowMultibreak = register(breaking.add(Setting("Allow Multi Break", this, false)))

    private val cases0 = register(SettingGroup(Setting("Cases", this)))
    private val simpleCase1 = register(cases0.add(Setting("Simple Case 1", this, true)))
    private val simpleCase2 = register(cases0.add(Setting("Simple Case 2", this, true)))
    private val middleCase = register(cases0.add(Setting("Middle Case", this, true)))
    private val leftDiagonalCase = register(cases0.add(Setting("Left Diagonal", this, true)))
    private val rightDiagonalCase = register(cases0.add(Setting("Right Diagonal", this, true)))

//    private val damages = register(SettingGroup(Setting("Damages", this)))
//    private val minDMG = register(damages.add(Setting("Min DMG", this, 10.0, 0.0, 20.0, true)))
//    private val maxSelfDMG = register(damages.add(Setting("Max Self DMG", this, 7.0, 0.0, 20.0, true)))

    private val trapping = /*register*/(Setting("Trapping", this, false))
    //TODO: rewrite burrow miner
    private val burrow = /*register*/(Setting("Burrow", this, false))
//    private val debug3 = register(Setting("Debug 3", this, false))

    private val autorerSync = register(SettingGroup(Setting("Auto ReR Sync", this)))
    private val autorerTargetSync = register(autorerSync.add(Setting("Auto Rer Target Sync", this, false).setTitle("Target")))

    private val renderersGroup = register(SettingGroup(Setting("Renderers", this)))

    private val currentBlockRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Current Block", this))))
    private val currentFacingRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Current Facing", this))))
    private val otherFacingsRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Other Facings", this))))
//    private val crystalPosRendererGroup = register(renderersGroup.add(SettingGroup(Setting("Crystal Block", this))))

    private val currentBlockPattern = SlideRenderingRewritePattern(this).prefix("Current Block").group(currentBlockRendererGroup).preInit().init()
    private val currentFacingPattern = RenderingRewritePattern(this).prefix("Current Facing").group(currentFacingRendererGroup).preInit().init()
    private val otherFacingsPattern = RenderingRewritePattern(this).prefix("Other Facings").group(otherFacingsRendererGroup).preInit().init()
//    private val crystalPosPattern = SlideRenderingRewritePattern(this).prefix("Crystal Pos").group(crystalPosRendererGroup).preInit().init()

    private val placementGroup = register(SettingGroup(Setting("Base Placement", this)))
    private val placementState = register(placementGroup.add(Setting("Base Placement", this, false).setTitle("State")))
    private val placementPattern = ObbyPlacementPattern(this, true).group(placementGroup).preInit().init()

    private var clicked = false
    private var lastPos : BlockPos? = null

    private val cases = mutableListOf<Case>()
    private var bestCase : Case? = null

    private val posses = mutableListOf<BlockPos>()
    private val currentPosses = mutableListOf<BlockPos>()
    private val basePosses = mutableListOf<BlockPos>()
    private val processingBlocks = mutableListOf<BlockPos>()
    private var processingBlock : BlockPos? = null
    private var baseBlock : BlockPos? = null

    private val currentBlockRenderer = SlideRendererPattern()
//    private val crystalPosRenderer = SlideRendererPattern()

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
        processingBlocks.clear()
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

        if(canBeBurrowed(player!!) && trapping.valBoolean) {
            // TODO: auto trap action
            displayInfo("Trapping")
        } else {
            if(isBurrowed(player!!) && burrow.valBoolean) {
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
        if (pos == null) {//TODO: can block be broken?
            lastPos = null
            return
        }

        val result = mc.world.rayTraceBlocks(Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight().toDouble(), mc.player.posZ), Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5))

        if(!usePacketMine.valBoolean) {
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
        } else if(PacketMineRewrite3.instance!!.current() == null || !PacketMineRewrite3.instance!!.queue().contains(pos)) {
//            if(
//                if(allowMultibreak.valBoolean) !PacketMineRewrite3.instance!!.queue().contains(pos)
//                else PacketMineRewrite3.instance!!.current() != lastPos
//            ) {
                mc.playerController.onPlayerDamageBlock(pos, result?.sideHit ?: EnumFacing.UP)
//            }
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
        val posses0 = dynamicBlocksSorted(player)
        var minObbis = Int.MAX_VALUE

        cases.clear()
        bestCase = null

        for(entry in posses0) {
            val facing = entry.key!!

            for(pos in entry.value) {
                if(pos != null) {
                    val pos1 = pos.offset(facing.opposite)
                    val case = processFacing(facing, pos1)

                    if(case != null) {
                        cases.add(Case(case, pos1, facing).also {
                            val obbis = case.howManyObbis(facing, pos1, newVersion.valBoolean)

                            if(obbis < minObbis) {
                                bestCase = it
                                minObbis = obbis
                            }
                        })
                    }
                }
            }
        }

        run {
            for(case in cases) {
                for(pos in case.case.posses(case.facing)) {
                    val pos1 = pos.add(case.pos)

                    if(pos is CrystalBlockPos) {
                        //TODO: check if finalPos is obby/bedrock ^^^^
                        baseBlock = pos1
                    } else if(block(pos1) != Blocks.AIR) {
                        processingBlock = pos1
                        mineBlock(pos1)

                        if(usePacketMine.valBoolean && allowMultibreak.valBoolean) {
                            processingBlocks.add(pos1)
                        }

                        return@run
                    }
                }
            }

            processingBlock = null
        }
    }

    private fun processFacing(
        facing : EnumFacing,
        pos : BlockPos
    ) : Cases? = if(newVersion.valBoolean) {
        if(Cases.SimpleCase1.isIt(facing, pos, newVersion.valBoolean)) {
            Cases.SimpleCase1
        } else {
            null
        }
    } else {
        var obbis = Int.MAX_VALUE
        var bestCase : Cases? = null

        for (case in Cases.values()) {
            if (caseSettings[case]!!.valBoolean && (if (down.valInt == 0) case.isIt(
                    facing,
                    pos,
                    newVersion.valBoolean
                ) && (!blockReachDistanceCheck.valBoolean || case.isItInRange(
                    facing,
                    pos,
                    blockReachDistance.valDouble,
                    newVersion.valBoolean
                )) else case.isIt(
                    facing,
                    pos,
                    newVersion.valBoolean,
                    down.valInt
                ) && (!blockReachDistanceCheck.valBoolean || case.isItInRange(
                    facing,
                    pos,
                    blockReachDistance.valDouble,
                    newVersion.valBoolean,
                    down.valInt
                )))
            ) {
                val newObbis = if (down.valInt == 0){
                    case.howManyObbis(facing, pos, newVersion.valBoolean)
                } else {
                    case.howManyObbis(facing, pos, newVersion.valBoolean, down.valInt)
                }

                if (newObbis < obbis) {
                    obbis = newObbis
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
            for (case in cases) {
                for (pos in case.case.posses(case.facing)) {
                    if (pos !is CrystalBlockPos) {
                        val pos1 = pos.add(case.pos)

                        if (usePacketMine.valBoolean && allowMultibreak.valBoolean) {
                            if(processingBlocks.contains(pos1)) {
                                currentBlockRenderer.handleRenderWorldStatic(
                                    currentBlockPattern,
                                    pos1,
                                    null
                                )
                            }

                            continue
                        } else if (pos1 == processingBlock) {
                            currentBlockRenderer.handleRenderWorld(
                                currentBlockPattern,
                                pos1,
                                null
                            )

                            continue
                        }

                        if (case == bestCase) {
                            currentFacingPattern.draw(pos1)

                            continue
                        }

                        otherFacingsPattern.draw(pos1)
                    }
                }
            }
        }
    }
}