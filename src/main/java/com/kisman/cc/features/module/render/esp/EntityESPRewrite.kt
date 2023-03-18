package com.kisman.cc.features.module.render.esp

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.util.EntityESPRendererPattern
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(
    name = "EntityESPRewrite",
    display = "Entities",
    desc = "Highlights entities",
    submodule = true
)
class EntityESPRewrite : Module() {
    private val pattern : EntityESPRendererPattern = EntityESPRendererPattern(this)

    init {
        pattern.init()
    }

    override fun onEnable() {
        super.onEnable()
        pattern.onEnable()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        pattern.draw(event.partialTicks)
    }
}