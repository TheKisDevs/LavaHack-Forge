package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.features.module.combat.autoanchor.PlaceInfo
import com.kisman.cc.features.subsystem.subsystems.RotationSystem
import com.kisman.cc.features.subsystem.subsystems.Target
import com.kisman.cc.features.subsystem.subsystems.Targetable
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.PlacementPattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.enums.AutoAnchorGlowStonePlacement
import com.kisman.cc.util.enums.AutoAnchorPlacement
//import com.kisman.cc.util.enums.Safety
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.world.*
import com.kisman.cc.util.world.block.RESPAWN_ANCHOR
import net.minecraft.block.BlockDynamicLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemFood
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.Supplier

/**
 * Shader system flags:
 *
 * 1 - anchor
 *
 * 2 - glowstone
 *
 * 3 - helping blocks
 *
 * @author _kisman_
 * @since 13:23 of 15.01.2023
 */
@Suppress("UNUSED_PARAMETER")
@Targetable
class AutoAnchor : ShaderableModule(
    "AutoAnchor",
    "Killing enemies with anchors. Only for 1.16+ servers",
    Category.COMBAT,
    true
) {
    private val targetRange = register(Setting("Target Range", this, 10.0, 1.0, 50.0, true))
    private val delay = register(Setting("Delay", this, 100.0, 0.0, 1000.0, NumberType.TIME))
    private val placer = PlacementPattern(this, true).preInit().init()
    private val threads = threads()
    private val renderers = register(SettingGroup(Setting("Renderers", this)))
    private val anchorRendererGroup = register(renderers.add(SettingGroup(Setting("Anchor", this))))
    private val anchorPattern = SlideRenderingRewritePattern(this).group(anchorRendererGroup).prefix("Anchor").preInit().init()
    private val glowstoneRendererGroup = register(renderers.add(SettingGroup(Setting("GlowStone", this))))
    private val glowstonePattern = SlideRenderingRewritePattern(this).group(glowstoneRendererGroup).prefix("GlowStone").preInit().init()
    private val helpingBlocksRendererGroup = register(renderers.add(SettingGroup(Setting("Helping Blocks", this))))
    private val helpingBlocksPattern = SlideRenderingRewritePattern(this).group(helpingBlocksRendererGroup).prefix("Helping Blocks").preInit().init()
    private val airPlace = register(Setting("Air Place", this, false))
    private val helpingBlocksOffset = register(Setting("Helping Blocks Offset", this, "X", listOf("X", "Z")))
    private val glowstonePlacement = register(SettingEnum("GlowStone Placement", this, AutoAnchorGlowStonePlacement.Normal).setTitle("GlowStone"))
//    private val safety = register(Setting("Safety", this, Safety.Suicide))
    private val placement = register(Setting("Placement", this, AutoAnchorPlacement.AboveHead))
    private val placeRange = register(Setting("Place Range", this, 5.0, 1.0, 6.0, false))
    private val minDamage = register(Setting("Min Damage", this, 36.0, 0.0, 36.0, true))
    private val maxDamage = register(Setting("Max Damage", this, 8.0, 0.0, 36.0, true))
//    private val raytraceState = register(Setting("RayTrace", this, false))
    private val terrain = register(Setting("Terrain", this, true))
    private val entityCheck = register(Setting("Entity Check", this, true))
    private val interpolationTicks = register(Setting("Interpolation Ticks", this, 0.0, 0.0, 10.0, true))

    private val anchorRenderer = SlideRendererPattern()
    private val glowstoneRenderer = SlideRendererPattern()

    private val targets = TargetFinder(targetRange.supplierDouble, threads)

    @Target var target : EntityPlayer? = null

    private var renderAnchorPos : BlockPos? = null
    private var renderGlowStonePos : BlockPos? = null
    private var anchorPos : BlockPos? = null

    private var lastTargetPos : BlockPos? = null
    private var lastPlaceInfo : PlaceInfo? = null

    private var timer = TimerUtils()

    private val helpingPosses = mapOf(
        0 to SlideRendererPattern(),
        1 to SlideRendererPattern(),
        2 to SlideRendererPattern()
    )

    private val renderHelpingPosses = mutableMapOf<BlockPos, Int>()

    private val possesForCheck = listOf(
        BlockPos(1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, -1)
    )

    init {
        super.setDisplayInfo { "[${if(target == null) "no target no fun" else target!!.name}]" }

        addFlags(
            Supplier { anchorPattern.canRender() },
            Supplier { glowstonePattern.canRender() },
            Supplier { helpingBlocksPattern.canRender() }
        )
    }

    override fun onEnable() {
        super.onEnable()
        reset()
        anchorRenderer.reset()
        glowstoneRenderer.reset()
        timer.reset()

        for(renderer in helpingPosses.values) {
            renderer.reset()
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            reset()
            return
        }

        targets.update()

        target = targets.target

        if(target == null) {
            reset()
            return
        }

        if(timer.passedMillis(delay.valLong)) {
            val targetPos = entityPosition(target!!)

            val placeInfo = if(placement.valEnum == AutoAnchorPlacement.AboveHead) {
                PlaceInfo(
                    target!!,
                    targetPos.up(2),
                    targetPos.up(3),
                    -1f,
                    -1f
                )
            } else {
                if(targetPos != lastTargetPos) {
                    calculatePlacement()
                } else {
                    lastPlaceInfo
                }
            }

            if(placeInfo == null) {
                reset()
                return
            }

            anchorPos = placeInfo.anchorPos
            val glowstonePos = placeInfo.glowstonePos

            renderHelpingPosses.clear()

            var shouldPlaceHelpingBlocks = false

            if(!airPlace.valBoolean && placement.valEnum == AutoAnchorPlacement.AboveHead) {
                shouldPlaceHelpingBlocks = true

                for(offset in possesForCheck) {
                    val pos = anchorPos!!.add(offset)

                    if(mc.world.getBlockState(pos).block != Blocks.AIR) {
                        shouldPlaceHelpingBlocks = false
                    }
                }

                if(shouldPlaceHelpingBlocks) {
                    fun offset(
                        length : Double
                    ) : Int = if(length > 0.7) {
                        -1
                    } else if(length < 0.7) {
                        1
                    } else {
                        0
                    }

                    val xLength = target!!.posX - anchorPos!!.x
                    val zLength = target!!.posZ - anchorPos!!.z

                    val xOffset = offset(xLength)
                    val zOffset = offset(zLength)

                    val offset = BlockPos(if(helpingBlocksOffset.valString == "X") xOffset else 0, 0, if(helpingBlocksOffset.valString == "Z") zOffset else 0)

                    for(y in helpingPosses.keys) {
                        val pos = anchorPos!!.down(2).add(0, y, 0).add(offset)

                        if(placeable(pos)) {
                            val slot = InventoryUtil.findBlockExtendedExclude(Blocks.OBSIDIAN, 0, 9, RESPAWN_ANCHOR)

                            placer.placeBlockSwitch(pos, slot)

                            timer.reset()

                            if(slot != -1) {
                                renderHelpingPosses[pos] = y
                            }
                        }
                    }
                }
            }

            if(airPlace.valBoolean || !shouldPlaceHelpingBlocks) {
                if (placeCheck(anchorPos!!)) {
                    val slot = InventoryUtil.findBlockExtended(RESPAWN_ANCHOR, 0, 9)

                    placer.placeBlockSwitch(anchorPos!!, slot)

                    timer.reset()

                    renderAnchorPos = if (slot != -1) {
                        anchorPos
                    } else {
                        null
                    }
                } else if (mc.world.getBlockState(anchorPos!!).block == Blocks.OBSIDIAN) {
                    val slot = InventoryUtil.findBlock(Blocks.GLOWSTONE, 0, 9)

                    placer.placeBlockSwitch(glowstonePos, slot)

                    when(glowstonePlacement.valEnum) {
                        AutoAnchorGlowStonePlacement.Fast -> {
                            placer.placeBlockSwitch(glowstonePos, slot)
                            placer.placeBlockSwitch(glowstonePos, slot)
                            placer.placeBlockSwitch(glowstonePos, slot)
                            placer.placeBlockSwitch(glowstonePos, slot)
                        }

                        AutoAnchorGlowStonePlacement.Bypass -> {
                            fun freeSlot() : Int {
                                for(i in 0..9) {
                                    val item = mc.player.inventory.getStackInSlot(i).item

                                    if((item !is ItemBlock && item !is ItemFood) || mc.player.inventory.getStackInSlot(i).isEmpty) {
                                        return i
                                    }
                                }

                                return -1
                            }

                            val freeSlot = freeSlot()
//                            val oldSlot = mc.player.inventory.currentItem

                            if(freeSlot != -1) {
                                placer.placeBlockSwitch(glowstonePos, freeSlot)
                                RotationSystem.handleRotate(anchorPos!!)
                                mc.player.connection.sendPacket(CPacketPlayerTryUseItem())
//                                CPacketPlayerTryUseItem
//                                SwapEnum2.Swap.Normal.task.doTask(slot, false)
//                                mc.playerController.processRightClickBlock(mc.player, mc.world, glowstonePos, raytrace(glowstonePos, raytraceState.valBoolean, EnumFacing.UP), Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND)
//                                SwapEnum2.Swap.Normal.task.doTask(oldSlot, false)
                            }
                        }

                        else -> { }
                    }

                    timer.reset()

                    renderGlowStonePos = if (slot != -1) {
                        this.anchorPos!!.up()
                    } else {
                        null
                    }
                } else {
                    renderAnchorPos = null
                    renderGlowStonePos = null
                }
            }

            lastTargetPos = targetPos
            lastPlaceInfo = placeInfo
        }
    }

    private fun reset() {
        anchorPos = null

        renderAnchorPos = null
        renderGlowStonePos = null

        lastTargetPos = null
        lastPlaceInfo = null
    }

    private fun calculatePlacement() : PlaceInfo? {
        fun offset(
            center : BlockPos
        ) : EnumFacing? {
            for(facing in EnumFacing.values()) {
                if(placeCheck(center.offset(facing))) {
                    return facing
                }
            }

            return null
        }

        fun placeable(
            center : BlockPos
        ) : Boolean {
            for(facing in EnumFacing.values()) {
                if(
                    !placeCheck(center.offset(facing))
                    &&
                    (
                                    !entityCheck.valBoolean
                                    ||
                                    mc.world.getEntitiesWithinAABB(
                                        Entity::class.java,
                                        AxisAlignedBB(
                                            center.x.toDouble(),
                                            center.y.toDouble(),
                                            center.z.toDouble(),
                                            center.x + 1.0,
                                            center.y + 1.0,
                                            center.z + 1.0
                                        )
                                    ).size == 0
                    )
                ) {
                    return true
                }
            }

            return false
        }

        var minSelfDamage = Float.MAX_VALUE
        var maxTargetDamage = Float.MIN_VALUE
        var anchorPos : BlockPos? = null
        var glowstonePos : BlockPos? = null

        for(center in sphere(placeRange.valFloat)) {
            val offset = offset(center)

            if(placeCheck(center) && placeable(center) && offset != null) {
                val pos = center.offset(offset)

                val selfDamage = damageByAnchor(terrain.valBoolean, center)

                if(selfDamage <= maxDamage.valInt && selfDamage <= minSelfDamage) {
                    val targetDamage = damageByAnchor(target!!, terrain.valBoolean, center, interpolationTicks.valInt)

                    if(targetDamage > minDamage.valInt && targetDamage > maxTargetDamage) {
                        minSelfDamage = selfDamage
                        maxTargetDamage = targetDamage

                        anchorPos = center
                        glowstonePos = pos
                    }
                }
            }
        }

        return if(anchorPos == null || glowstonePos == null) {
            null
        } else {
            PlaceInfo(
                target!!,
                anchorPos,
                glowstonePos,
                minSelfDamage,
                maxTargetDamage
            )
        }
    }

    private fun placeCheck(
        pos : BlockPos
    ) : Boolean = mc.world.getBlockState(pos).block == Blocks.AIR || mc.world.getBlockState(pos).block is BlockLiquid || mc.world.getBlockState(pos).block is BlockDynamicLiquid

    @SubscribeEvent fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        handleDraw()
    }

    override fun draw0(
        flags : Array<Boolean>
    ) {
        if(flags[0]) {
            anchorRenderer.handleRenderWorld(anchorPattern, renderAnchorPos, null)
        }

        if(flags[1]) {
            glowstoneRenderer.handleRenderWorld(glowstonePattern, renderGlowStonePos, null)
        }

        if(flags[2]) {
            for(entry in renderHelpingPosses.entries) {
                val pos = entry.key
                val renderer = helpingPosses[entry.value]

                renderer!!.handleRenderWorld(helpingBlocksPattern, pos, null)
            }
        }
    }
}