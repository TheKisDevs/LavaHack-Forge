package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.subsystem.SubSystem
import com.kisman.cc.util.math.coerceIn
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketTimeUpdate

/**
 * @author _kisman_
 * @since 18:30 of 02.03.2023
 */
object TPSManager : SubSystem(
    "TPS Manager"
) {
    private val rates = FloatArray(20)
    private var next = 0
    private var last = 0L

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        if(it.packet is SPacketTimeUpdate) {
            process()
        }
    })

    init {
        listeners(receive)
    }

    fun process() {
        if(last != -1L) {
            rates[next % rates.size] = (20f / ((System.currentTimeMillis() - last) / 1000f)).coerceIn(0f, 20f)
            next++
        }

        last = System.currentTimeMillis()
    }

    fun reset() {
        rates.fill(0f)
        next = 0
        last = -1L
    }

    val tps : Float
        get() {
            var num = 0f
            var sum = 0f

            for(rate in rates) {
                if(rate > 0f) {
                    sum += rate
                    num++
                }
            }

            return (sum / num).coerceIn(0f, 20f)
        }
}

val tps : Float //For kotlin modules
    get() = TPSManager.tps