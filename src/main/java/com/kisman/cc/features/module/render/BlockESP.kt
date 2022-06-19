package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.util.BoxRendererPattern
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.util.world.CrystalUtils
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.Supplier

class BlockESP : Module(
        "BlockESP",
        Category.RENDER
) {
    private val range : Setting = register(Setting("Range", this, 30.0, 0.0, 50.0, true))

    private val mtPattern = MultiThreaddableModulePattern(this)

    private val web : Setting = register(Setting("Web", this, false))
    private val webRenderer = BoxRendererPattern(this, Supplier { web.valBoolean }, "Web", true).init()

    private val burrow : Setting = register(Setting("Burrow", this, false))
    private val burrowRenderer = BoxRendererPattern(this, Supplier { burrow.valBoolean }, "Burrow", true).init()

    private val portal : Setting = register(Setting("Portal", this, false))
    private val portalRenderer = BoxRendererPattern(this, Supplier { portal.valBoolean }, "Portal", true).init()

    private val endPortal : Setting = register(Setting("End Portal", this, false))
    private val endPortalRenderer = BoxRendererPattern(this, Supplier { endPortal.valBoolean }, "End Portal", true).init()

    private var list = java.util.ArrayList<BlockPos>()

    override fun onEnable() {
        super.onEnable()
        list.clear()
        mtPattern.reset()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        mtPattern.update(Runnable {
            doBlockESPLogic()
        })
        doBlockESP(event.partialTicks)
    }

    private fun doBlockESP(ticks : Float) {
        for(pos in list) {
            val block = mc.world.getBlockState(pos).block
            if(block == Blocks.WEB && web.valBoolean) webRenderer.draw(ticks, pos)
            if(block == Blocks.PORTAL && portal.valBoolean) portalRenderer.draw(ticks, pos)
            if(block == Blocks.END_PORTAL && endPortal.valBoolean) endPortalRenderer.draw(ticks, pos)
            if(burrow.valBoolean && mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)).isNotEmpty() && block != Blocks.AIR) burrowRenderer.draw(ticks, pos)
        }
    }

    private fun doBlockESPLogic() {
        val list = java.util.ArrayList<BlockPos>(list.size)
        for(pos in CrystalUtils.getSphere(range.valFloat, true, false)) {
            val block = mc.world.getBlockState(pos).block
            if(block == Blocks.WEB && web.valBoolean) list.add(pos)
            if(block == Blocks.PORTAL && portal.valBoolean) list.add(pos)
            if(block == Blocks.END_PORTAL && endPortal.valBoolean) list.add(pos)
            if(burrow.valBoolean && mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)).isNotEmpty() && block != Blocks.AIR) list.add(pos)
        }
        mc.addScheduledTask {
            this.list = list
        }
    }
}