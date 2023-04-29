package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.event.events.EventRenderBlock
import com.kisman.cc.features.subsystem.SubSystem
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import kotlin.collections.HashMap

/**
 * TODO: rewrite it with using Holes
 *
 * @author _kisman_
 * @since 20:34 of 09.12.2022
 */
object HoleProcessor : SubSystem("Hole Processor") {
    /*val holes = HashMap<BlockPos, HoleInfo>()

    @EventHandler
    private val renderBlock = Listener<EventRenderBlock>(EventHook {
        if(
            mc.player != null
            && mc.world != null
            && mc.world.getBlockState(it.pos).block == Blocks.AIR
            && mc.world.getBlockState(it.pos.down()).block != Blocks.AIR
            && mc.world.getBlockState(it.pos.up()).block == Blocks.AIR
        ) {
//            val info = HoleUtil.isHole(it.pos, false, false)
//            val type = info.type

            *//*if(type != HoleType.NONE) {
                if(!holes.contains(info.posses[0]) && !(type != HoleType.DOUBLE || holes.contains(info.posses[1]))) {
                    holes[it.pos] = info!!
                }
            }*//*
        }
    })

    init {
//        listeners(renderBlock)
    }

    override fun renderWorld(
        event : RenderWorldLastEvent
    ) {
        holes.clear()
    }*/
}