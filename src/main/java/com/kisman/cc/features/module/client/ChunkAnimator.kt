package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderChunk
import com.kisman.cc.event.events.EventRenderChunkContainer
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.enums.dynamic.EasingEnum
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.chunk.RenderChunk

/**
 * @author _kisman_
 * @since 22:21 of 03.12.2022
 */
@ModuleInfo(
    name = "ChunkAnimator",
    desc = "Implementation of the chunk animator mod.",
    category = Category.CLIENT,
    wip = true
)
class ChunkAnimator : Module() {
    private val length = register(Setting("Length", this, 1000.0, 250.0, 5000.0, NumberType.TIME))
    private val easing = register(SettingEnum("Easing", this, EasingEnum.Easing.Linear))

    private val chunks = HashMap<RenderChunk, Long>()

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderChunkContainer)
        Kisman.EVENT_BUS.subscribe(renderChunk)
        chunks.clear()
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(renderChunkContainer)
        Kisman.EVENT_BUS.unsubscribe(renderChunk)
    }

    private val renderChunkContainer = Listener<EventRenderChunkContainer>(EventHook {
        if(chunks.containsKey(it.chunk)) {
            val start = chunks[it.chunk]!!
            val diff = System.currentTimeMillis() - start

            println(diff)

            if(diff < length.valLong) {
                println(easing.valEnum.task.doTask((diff / length.valInt).toDouble()))
                GlStateManager.translate(0.0, it.chunk.position.y  * easing.valEnum.task.doTask((diff / length.valInt).toDouble()), 0.0)
            }
        }
    })

    private val renderChunk = Listener<EventRenderChunk>(EventHook {
        if(!chunks.containsKey(it.chunk)) {
            println("adding new chunk")
            chunks[it.chunk] = System.currentTimeMillis()
        }
    })
}