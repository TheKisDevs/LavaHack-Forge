package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderChunk
import com.kisman.cc.event.events.EventRenderChunkContainer
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.enums.dynamic.EasingEnum
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.chunk.RenderChunk
import java.util.concurrent.atomic.AtomicLong

/**
 * @author _kisman_
 * @since 22:21 of 03.12.2022
 */
@WorkInProgress
class ChunkAnimator : Module(
    "ChunkAnimator",
    "Implementation of the chunk animator mod.",
    Category.CLIENT
) {
    private val length = register(Setting("Length", this, 1000.0, 250.0, 5000.0, NumberType.TIME))
    private val easing = SettingEnum("Easing", this, EasingEnum.Easing.Linear).register()

    private val chunks = HashMap<RenderChunk, AtomicLong>()

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderChunkContainer)
        Kisman.EVENT_BUS.subscribe(renderChunk)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(renderChunkContainer)
        Kisman.EVENT_BUS.unsubscribe(renderChunk)
    }

    private val renderChunkContainer = Listener<EventRenderChunkContainer>(EventHook {
        if(chunks.containsKey(it.chunk)) {
            val atomicTime = chunks[it.chunk]!!
            var time = atomicTime.get()

            if(time == -1L) {
                time = System.currentTimeMillis()
                atomicTime.set(time)
            }

            val diff = System.currentTimeMillis() - time

            if(diff < length.valInt) {
                GlStateManager.translate(0.0, -it.chunk.position.y + (it.chunk.position.y  * easing.valEnum.task.doTask((diff / length.valInt).toDouble())), 0.0)
            }
        }
    })

    private val renderChunk = Listener<EventRenderChunk>(EventHook {
        if(mc.player != null && !chunks.containsKey(it.chunk)) {
            chunks[it.chunk] = AtomicLong(-1)
        }
    })
}