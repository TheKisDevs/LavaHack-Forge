package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
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

    private val webGroup = register(SettingGroup(Setting("Web", this)))
    private val web = register(webGroup.add(Setting("Web", this, false)))
    private val webRenderer = BoxRendererPattern(this).group(webGroup).visible(Supplier { web.valBoolean }).prefix("Web").preInit().init()

    private val burrowGroup = register(SettingGroup(Setting("Burrow", this)))
    private val burrow = register(burrowGroup.add(Setting("Burrow", this, false)))
    private val burrowRenderer = BoxRendererPattern(this).group(burrowGroup).visible(Supplier { burrow.valBoolean }).prefix("Burrow").preInit().init()

    private val portalGroup = register(SettingGroup(Setting("Portal", this)))
    private val portal = register(portalGroup.add(Setting("Portal", this, false)))
    private val portalRenderer = BoxRendererPattern(this).group(portalGroup).visible(Supplier { portal.valBoolean }).prefix("Portal").preInit().init()

    private val endPortalGroup = register(SettingGroup(Setting("End Portal", this)))
    private val endPortal = register(endPortalGroup.add(Setting("End Portal", this, false)))
    private val endPortalRenderer = BoxRendererPattern(this).group(endPortalGroup).visible(Supplier { endPortal.valBoolean }).prefix("End Portal").preInit().init()

    private var list = ArrayList<BlockPos>()

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
        val list = ArrayList<BlockPos>(list.size)
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