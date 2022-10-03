package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.util.ShadersRendererPattern
import net.minecraftforge.client.event.RenderHandEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 23:04 of 16.08.2022
 */
@WorkInProgress
class Shaders : Module(
    "Shaders",
    "Uhhhh",
    Category.RENDER
) {
    private val renderer = ShadersRendererPattern(this).init()

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
//        renderer.render(event.partialTicks)
    }

    @SubscribeEvent fun onRenderHand(event : RenderHandEvent) {
        event.isCanceled = renderer.renderHand()
    }
}