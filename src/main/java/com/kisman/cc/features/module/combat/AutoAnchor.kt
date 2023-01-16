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
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 13:23 of 15.01.2023
 */
@Targetable
class AutoAnchor : Module(
    "AutoAnchor",
    "Only for 1.16+ servers",
    Category.COMBAT
) {
    private val targetRange = register(Setting("Target Range", this, 10.0, 1.0, 50.0, true))
    private val delay = register(Setting("Delay", this, 100.0, 0.0, 1000.0, NumberType.TIME))
    private val placer = PlacementPattern(this, true).preInit().init()
    private val threads = threads()
    private val anchorRendererGroup = register(SettingGroup(Setting("Anchor Renderer", this)))
    private val anchorPattern = SlideRenderingRewritePattern(this).group(anchorRendererGroup).prefix("Anchor").preInit().init()
    private val glowstoneRendererGroup = register(SettingGroup(Setting("GlowStone Renderer", this)))
    private val glowstonePattern = SlideRenderingRewritePattern(this).group(glowstoneRendererGroup).prefix("GlowStone").preInit().init()

    private val anchorRenderer = SlideRendererPattern()
    private val glowstoneRenderer = SlideRendererPattern()

    private val targets = TargetFinder(targetRange.supplierDouble, threads)

    @Target var target : EntityPlayer? = null

    private var placePos : BlockPos? = null
    private var renderAnchorPos : BlockPos? = null
    private var renderGlowStonePos : BlockPos? = null
    private var anchorPos : BlockPos? = null

    private var timer = TimerUtils()

    init {
        super.setDisplayInfo { "[${if(target == null) "no target no fun" else target!!.name}]" }
    }

    override fun onEnable() {
        super.onEnable()
        anchorRenderer.reset()
        glowstoneRenderer.reset()
        timer.reset()
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
    @SubscribeEvent fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        anchorRenderer.handleRenderWorld(anchorPattern, renderAnchorPos, null)
        glowstoneRenderer.handleRenderWorld(glowstonePattern, renderGlowStonePos, null)
    }
}