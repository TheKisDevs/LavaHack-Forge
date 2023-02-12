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
        displayName = "EntityESP"

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