package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.mixin.mixins.accessor.AccessorEnumConnectionState
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.chat.cubic.ChatUtility
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.Packet
import java.lang.reflect.Field

/**
 * @author _kisman_
 * @since 22:00 of 24.08.2022
 */
class PacketFeatures : Module(
    "PacketFeatures",
    "Cancels/logs minecraft packets :>",
    Category.PLAYER
) {
    private val canceller = register(SettingGroup(Setting("Canceller", this)))

    private val cancellerState = register(canceller.add(Setting("Canceller State", this, false).setTitle("State")))

    private val cancellerPackets = register(canceller.add(SettingGroup(Setting("Packets", this))))

    private val cancellerCPackets = register(cancellerPackets.add(SettingGroup(Setting("CPackets", this))))
    private val cancellerSPackets = register(cancellerPackets.add(SettingGroup(Setting("SPackets", this))))

    private val cancellerPacketsMap = HashMap<Class<out Packet<*>>, Setting>()


    private val logger = register(SettingGroup(Setting("Logger", this)))

    private val loggerState = register(logger.add(Setting("Logger State", this, false).setTitle("State")))

    private val loggerPackets = register(logger.add(SettingGroup(Setting("Packets", this))))

    private val loggerCPackets = register(loggerPackets.add(SettingGroup(Setting("CPackets", this))))
    private val loggerSPackets = register(loggerPackets.add(SettingGroup(Setting("SPackets", this))))

    private val loggerPacketsMap = HashMap<Class<out Packet<*>>, Setting>()

    init {
        val map = (EnumConnectionState.PLAY as AccessorEnumConnectionState).directionMaps()

        for(direction in map.keys) {
            val cancellerGroup : SettingGroup
            val loggerGroup : SettingGroup

            if(direction == EnumPacketDirection.SERVERBOUND) {
                cancellerGroup = cancellerCPackets
                loggerGroup = loggerCPackets
            } else {
                cancellerGroup = cancellerSPackets
                loggerGroup = loggerSPackets
            }


            for(packet in map[direction]!!.values) {
                cancellerPacketsMap[packet] = register(cancellerGroup.add(Setting("Canceller ${packet.simpleName}", this, false).setTitle(packet.simpleName)))
                loggerPacketsMap[packet] = register(loggerGroup.add(Setting("Logger ${packet.simpleName}", this, false).setTitle(packet.simpleName)))
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(cancellerState.valBoolean && cancellerPacketsMap.containsKey(it.packet::class.java) && cancellerPacketsMap[it.packet::class.java]!!.valBoolean) {
            it.cancel()
        }

        if(loggerState.valBoolean && loggerPacketsMap.containsKey(it.packet::class.java) && loggerPacketsMap[it.packet::class.java]!!.valBoolean) {
            var message = "---------------------"

            message += it.packet::class.java.simpleName

            for(field in it.packet::class.java.fields) {
                message += "\n\t${processField(it.packet, field)}"
            }

            if(it.cancelled) {
                message += "\n\tCancelled"
            }

            message += "---------------------"

            ChatUtility.cleanMessage(message)
        }
    })

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        if(cancellerState.valBoolean && cancellerPacketsMap.containsKey(it.packet::class.java) && cancellerPacketsMap[it.packet::class.java]!!.valBoolean) {
            it.cancel()
        }

        if(loggerState.valBoolean && loggerPacketsMap.containsKey(it.packet::class.java) && loggerPacketsMap[it.packet::class.java]!!.valBoolean) {
            var message = "---------------------"

            message += it.packet::class.java.simpleName

            for(field in it.packet::class.java.fields) {
                message += "\n\t${processField(it.packet, field)}"
            }

            if(it.cancelled) {
                message += "\n\tCancelled"
            }

            message += "---------------------"

            ChatUtility.cleanMessage(message)
        }
    })

    private fun processField(
        obj : Any,
        field : Field
    ) : String {
        return "${field.name}: ${if(field.type == Integer.TYPE) {
            "${field.getInt(obj)}"
        } else if(field.type == java.lang.Short.TYPE) {
            "${field.getShort(obj)}"
        } else if(field.type == java.lang.Long.TYPE) {
            "${field.getLong(obj)}"
        } else if(field.type == java.lang.Double.TYPE) {
            "${field.getDouble(obj)}"
        } else if(field.type == java.lang.Float.TYPE) {
            "${field.getFloat(obj)}"
        } else if(field.type == java.lang.String::class.java) {
            "${field[obj]}"
        } else if(field.isEnumConstant) {
            (field[obj] as Enum<*>).name
        } else {
            "${field[obj]}"
        }}"
    }
}