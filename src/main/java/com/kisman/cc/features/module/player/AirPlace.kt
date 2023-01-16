package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.math.Trigonometric
import com.kisman.cc.util.render.pattern.SlideRendererPattern
import com.kisman.cc.util.world.BlockUtil2
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse

/**
 * @author _kisman_
 * @since 12:43 of 15.01.2023
 */
@WorkInProgress
class AirPlace : Module(
    "AirPlace",
    "Allows to place in air only for 1.13+ servers!",
    Category.PLAYER
) {
    private val radius = register(Setting("Radius", this, 4.5, 1.0, 6.0, false))
    private val packet = register(Setting("Packet", this, true))
    private val rendererGroup = register(SettingGroup(Setting("Renderer", this)))
    private val pattern = SlideRenderingRewritePattern(this).group(rendererGroup).preInit().init()

    private val renderer = SlideRendererPattern()

    private var lastPos : BlockPos? = null
    private var pos : BlockPos? = null

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        pos = Trigonometric.entityObjectMouseOver(mc.player, radius.valDouble, false)

        println("${pos != null} ${lastPos != pos}")

        if (pos != null && lastPos != pos) {
            if (mc.world.getBlockState(pos!!).block == Blocks.AIR) {
                if (Mouse.isButtonDown(1)) {
                    BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, packet.valBoolean, false, false)

                    lastPos = pos
                }
            } else {
                pos = null
            }
        }
    }

    @SubscribeEvent fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        renderer.handleRenderWorld(pattern, pos, null)
    }
}