package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.subsystem.subsystems.Target
import com.kisman.cc.features.subsystem.subsystems.Targetable
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.PlacementPattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.world.block.RESPAWN_ANCHOR
import com.kisman.cc.util.world.entityPosition
import com.kisman.cc.util.world.placeable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 13:23 of 15.01.2023
 */
@Suppress("UNUSED_PARAMETER")
@Targetable
class AutoAnchor : Module(
    "AutoAnchor",
    "Killing enemies with anchors. Only for 1.16+ servers",
    Category.COMBAT
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
    private val test = register(Setting("Fast Place", this, false))

    private val anchorRenderer = SlideRendererPattern()
    private val glowstoneRenderer = SlideRendererPattern()

    private val targets = TargetFinder(targetRange.supplierDouble, threads)

    @Target var target : EntityPlayer? = null

    private var placePos : BlockPos? = null
    private var renderAnchorPos : BlockPos? = null
    private var renderGlowStonePos : BlockPos? = null
    private var anchorPos : BlockPos? = null

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
    }

    override fun onEnable() {
        super.onEnable()
        anchorRenderer.reset()
        glowstoneRenderer.reset()
        timer.reset()

        for(renderer in helpingPosses.values) {
            renderer.reset()
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        targets.update()

        target = targets.target

        if(target == null) {
            anchorPos = null
            return
        }

        if(timer.passedMillis(delay.valLong)) {
            //TODO: damage checks like ca

            placePos = entityPosition(target!!).up(2)

            val placeBlock = mc.world.getBlockState(placePos!!).block

            renderHelpingPosses.clear()

            var shouldPlaceHelpingBlocks = true

            if(!airPlace.valBoolean) {
                for(offset in possesForCheck) {
                    val pos = placePos!!.add(offset)

                    if(mc.world.getBlockState(pos).block != Blocks.AIR) {
                        shouldPlaceHelpingBlocks = false
                    }
                }

                if(shouldPlaceHelpingBlocks) {
                    val xLength = target!!.posX - placePos!!.x
                    val zLength = target!!.posZ - placePos!!.z

                    val xOffset = if(xLength > 0.7) {
                        -1
                    } else if(xLength < 0.7) {
                        1
                    } else {
                        0
                    }

                    val zOffset = if(zLength > 0.7) {
                        -1
                    } else if(zLength < 0.7) {
                        1
                    } else {
                        0
                    }

                    val offset = BlockPos(if(helpingBlocksOffset.valString == "X") xOffset else 0, 0, if(helpingBlocksOffset.valString == "Z") zOffset else 0)

                    for(y in helpingPosses.keys) {
                        val pos = placePos!!.down(2).add(0, y, 0).add(offset)

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
                if (placeBlock == Blocks.AIR) {
                    val slot = InventoryUtil.findBlockExtended(RESPAWN_ANCHOR, 0, 9)

                    placer.placeBlockSwitch(placePos!!, slot)

                    timer.reset()

                    renderAnchorPos = if (slot != -1) {
                        placePos
                    } else {
                        null
                    }

                    anchorPos = renderAnchorPos
                } else if (anchorPos == placePos) {
                    val slot = InventoryUtil.findBlock(Blocks.GLOWSTONE, 0, 9)

                    placer.placeBlockSwitch(anchorPos!!.up(), slot)

                    if(test.valBoolean) {
                        placer.placeBlockSwitch(anchorPos!!.up(), slot)
                        placer.placeBlockSwitch(anchorPos!!.up(), slot)
                        placer.placeBlockSwitch(anchorPos!!.up(), slot)
                        placer.placeBlockSwitch(anchorPos!!.up(), slot)
                    }


                    timer.reset()

                    renderGlowStonePos = if (slot != -1) {
                        anchorPos!!.up()
                    } else {
                        null
                    }
                } else {
                    renderAnchorPos = null
                    renderGlowStonePos = null
                }
            }
        }
    }

    @SubscribeEvent fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        anchorRenderer.handleRenderWorld(anchorPattern, renderAnchorPos, null)
        glowstoneRenderer.handleRenderWorld(glowstonePattern, renderGlowStonePos, null)

        for(entry in renderHelpingPosses.entries) {
            val pos = entry.key
            val renderer = helpingPosses[entry.value]

            renderer!!.handleRenderWorld(helpingBlocksPattern, pos, null)
        }
    }
}