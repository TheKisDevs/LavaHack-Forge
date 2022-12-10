package com.kisman.cc.features.subsystem

import com.kisman.cc.Kisman
import me.zero.alpine.listener.Listenable
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * @author _kisman_
 * @since 20:30 of 09.12.2022
 */
abstract class SubSystem(
    val name : String
) : Listenable {
    fun init() {
        MinecraftForge.EVENT_BUS.register(this)
        Kisman.EVENT_BUS.subscribe(this)
    }

    @SubscribeEvent open fun update(
        event : TickEvent.ClientTickEvent
    ) {

    }

    @SubscribeEvent open fun renderWorld(
        event : RenderWorldLastEvent
    ) {

    }
}