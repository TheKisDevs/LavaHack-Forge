package com.kisman.cc.gui.loadingscreen.progressbar

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar
import com.kisman.cc.mixin.mixins.accessor.AccessorProgressBar
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraftforge.fml.common.ProgressManager

/**
 * @author _kisman_
 * @since 20:10 of 22.12.2022
 */
class ProgressBarController(
    val name : String
) {
    @JvmField var steps = 0

    private var bar : ProgressManager.ProgressBar? = null

    private val event = Listener<EventProgressBar>(EventHook {
        if(bar != null) {
            bar!!.step(it.title)
        }
    })

    fun init() {
        Kisman.EVENT_BUS.subscribe(event)
        bar = ProgressManager.push(name, steps, true)
    }

    fun uninit() {
        if(bar!!.steps != bar!!.step) {
            Kisman.LOGGER.error("ProgressBar: Trying to pop progress bar(${bar!!.steps}) with ${bar!!.step} passed steps!")

            (bar!! as AccessorProgressBar).step(bar!!.steps);
        }

        ProgressManager.pop(bar!!)

        Kisman.EVENT_BUS.unsubscribe(event)
    }
}