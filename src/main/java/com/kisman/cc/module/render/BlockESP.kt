package com.kisman.cc.module.render

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.util.BoxRendererPattern
import com.kisman.cc.util.CrystalUtils
import net.minecraft.block.BlockEndPortal
import net.minecraft.block.BlockPortal
import net.minecraft.block.BlockWeb
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.function.Supplier

class BlockESP : Module(
        "BlockESP",
        Category.RENDER
) {
    private val range : Setting = register(Setting("Range", this, 30.0, 0.0, 50.0, true))

    private val web : Setting = register(Setting("Web", this, false))
    private val webRenderer = BoxRendererPattern(this, Supplier { web.valBoolean }, "Web", true).init()

    private val burrow : Setting = register(Setting("Burrow", this, false))
    private val burrowRenderer = BoxRendererPattern(this, Supplier { burrow.valBoolean }, "Burrow", true).init()

    private val portal : Setting = register(Setting("Portal", this, false))
    private val portalRenderer = BoxRendererPattern(this, Supplier { portal.valBoolean }, "Portal", true).init()

    private val endPortal : Setting = register(Setting("End Portal", this, false))
    private val endPortalRenderer = BoxRendererPattern(this, Supplier { endPortal.valBoolean }, "End Portal", true).init()

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        for(pos in CrystalUtils.getSphere(range.valFloat, true, false)) {
            val block = mc.world.getBlockState(pos)
            if(block == Blocks.WEB && web.valBoolean) webRenderer.draw(event.partialTicks, pos)
            if(block == Blocks.PORTAL && portal.valBoolean) portalRenderer.draw(event.partialTicks, pos)
            if(block == Blocks.END_PORTAL && endPortal.valBoolean) endPortalRenderer.draw(event.partialTicks, pos)
            if(burrow.valBoolean && mc.world.getEntitiesWithinAABBExcludingEntity(null, block.getSelectedBoundingBox(mc.world, pos)).isNotEmpty() && block != Blocks.AIR) burrowRenderer.draw(event.partialTicks, pos)
        }
    }
}