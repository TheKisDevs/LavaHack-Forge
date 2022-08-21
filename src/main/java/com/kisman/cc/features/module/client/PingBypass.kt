package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.pingbypass.serializer.friend.FriendSerializer
import com.kisman.cc.features.pingbypass.serializer.setting.SettingSerializer
import com.kisman.cc.features.pingbypass.utility.disconnect
import com.kisman.cc.mixin.mixins.accessor.AccessorContainer
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.enums.PingBypassProtocol
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack
import net.minecraft.network.login.client.CPacketLoginStart
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketKeepAlive

/**
 * @author _kisman_
 * @since 20:30 of 19.08.2022
 */
object PingBypass : Module(
    "PingBypass",
    "Connects you to server via custom proxy",
    Category.CLIENT
) {
    var ip = ""
    var port = ""
        set(value) {
            try {
                portInt = Integer.valueOf(value)
                field = value
            } catch (ignored : NumberFormatException) {}
        }
    var portInt = 1234

    val timer = TimerUtils()
    var startTime = 0L
    var serverPing = -1
    var ping = -1L
    private var handled = false

    private val settingSerializer = SettingSerializer(ArrayList())
    private val friendSerializer = FriendSerializer()

    val protocol = register(Setting("Protocol", this, PingBypassProtocol.New))

    private val pings = register(Setting("Pings", this, 5000.0, 500.0, 10000.0, NumberType.TIME))
    private val noRender = register(Setting("No Render", this, false))
    val positionRange = register(Setting("Position Range", this, 5.0, 0.0, 10000.0, true))

    init {
        super.setDisplayInfo { "$ping ms" }
    }

    override fun onEnable() {
        super.onEnable()
        reset()
        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()
        reset()
        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private fun reset() {
        if(mc.player == null || mc.world == null) {
            return
        }

        disconnect()
        resetSerializers()
        timer.reset()
    }

    private fun resetSerializers() {
        settingSerializer.clear()
        friendSerializer.clear()
    }

    override fun update() {
        if(timer.passedMillis(pings.valLong)) {
            val connection = mc.connection

            if(connection != null) {
                val container = CPacketClickWindow(
                    1,
                    -1337,
                    1,
                    ClickType.PICKUP,
                    ItemStack.EMPTY,
                    (mc.player.openContainer as AccessorContainer).transactionID()
                )

                val alive = CPacketKeepAlive(-1337)

                startTime = System.currentTimeMillis()
                handled = false

                connection.sendPacket(container)
                connection.sendPacket(alive)
            }

            timer.reset()
        }
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(it.packet is CPacketLoginStart) {
            resetSerializers()
        }
    })

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet
        if(packet is SPacketKeepAlive) {
            if(!handled && packet.id > 0 && packet.id < 1000) {
                startTime = System.currentTimeMillis() - startTime
                serverPing = packet.id.toInt()
                ping = startTime
                handled = true

                it.cancel()
            }
        }
    })
}