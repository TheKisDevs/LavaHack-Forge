package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.util.EntityESPRendererPattern
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class EntityESPRewrite : Module(
        "EntityESPRewrite",
        Category.RENDER
) {
    private val pattern : EntityESPRendererPattern = EntityESPRendererPattern(this)

    init {
        pattern.init()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        pattern.draw(event.partialTicks)
    }
}