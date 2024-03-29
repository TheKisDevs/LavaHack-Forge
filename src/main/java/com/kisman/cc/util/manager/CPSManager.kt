package com.kisman.cc.util.manager

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.util.TimerUtils
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock

//just crystal per second manager
class CPSManager {
    val timer = TimerUtils()
    var usage: Int = 0
    var cps: Int = 0

    @EventHandler val send = Listener(EventHook { event: PacketEvent.Send ->
        if (event.packet is CPacketPlayerTryUseItemOnBlock) if (Minecraft.getMinecraft().player.heldItemMainhand.getItem() == Items.END_CRYSTAL || Minecraft.getMinecraft().player.heldItemOffhand.getItem() == Items.END_CRYSTAL) usage++
    })

    init {
        Kisman.EVENT_BUS.subscribe(send)
    }

    fun getCPS(): Int {
        if(timer.passedMillis(1000)) {
            if (usage == 0) {
                cps = 0;
                timer.reset()
            } else {
                cps = usage
                timer.reset()
                usage = 0
            }
        }
        return cps
    }
}