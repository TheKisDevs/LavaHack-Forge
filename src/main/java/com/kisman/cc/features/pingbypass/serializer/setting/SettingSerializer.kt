package com.kisman.cc.features.pingbypass.serializer.setting

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.settings.EventSettingChange
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.client.PingBypass
import com.kisman.cc.features.pingbypass.serializer.Serializer
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Globals.mc
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import java.util.Arrays
import kotlin.collections.HashSet

/**
 * @author _kisman_
 * @since 21:52 of 19.08.2022
 */
class SettingSerializer(
    private val modules : ArrayList<Module>
) : Serializer<Setting> {
    private val settings = HashSet<Setting>()
    private val changed = HashSet<Setting>()

    private val settingChange = Listener<EventSettingChange.Any>(EventHook {
        if(settings.contains(it.setting)) {
            mc.addScheduledTask {
                changed.add(it.setting)
            }
        }
    })

    init {
        Kisman.EVENT_BUS.subscribe(settingChange)
        MinecraftForge.EVENT_BUS.register(this)

        init()
    }

    @SubscribeEvent fun onClientTick(event : TickEvent.ClientTickEvent) {
        if(mc.player != null && mc.player.connection != null && changed.isNotEmpty()) {
            val next = pollSettings()

            if(next != null ) {
                serializeAndSend(next)
            }
        }
    }

    @SubscribeEvent fun onDisconnect(event : FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        clear()
    }

    fun init(
    ) {
        for(module in modules) {
            for(setting in Kisman.instance.settingsManager.getSettingsByMod(module)) {
                settings.add(setting)
            }
        }
    }

    fun clear() {
        synchronized(changed) {
            changed.clear()
            changed.addAll(settings)
        }
    }

    private fun pollSettings() : Setting? {
        if(changed.isNotEmpty()) {
            val next = changed.iterator().next()
            changed.remove(next)
            return next
        }

        return null
    }


    override fun serializeAndSend(
        setting : Setting
    ) {
        if(PingBypass.isToggled) {
            mc.player.connection.sendPacket(CPacketChatMessage("@Server${setting.parent.name} ${setting.name} $setting"))
        }
    }
}