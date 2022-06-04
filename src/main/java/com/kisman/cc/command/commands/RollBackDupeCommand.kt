package com.kisman.cc.command.commands

import com.kisman.cc.command.Command
import io.netty.buffer.ByteBuf
import net.minecraft.entity.passive.AbstractHorse
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.Packet


/**
 * @author _kisman_
 * @since 13:18 of 04.06.2022
 */
class RollBackDupeCommand : Command("rdupe") {
    companion object {
        var instance : RollBackDupeCommand? = null
    }

    private val toCancel = arrayListOf(12, 13, 14, 15)

    init {
        instance = this
    }

    private var state = false

    override fun runCommand(s: String?, args: Array<out String>?) {
        if(args?.size!! > 1 && args[1] == "reset") {
            state = false
        } else {
            //TODO исключения для пакен кенцела

            if(!state) {
                val horses = mc.player.world.getEntitiesWithinAABB(
                    AbstractHorse::class.java, mc.player.entityBoundingBox.grow(6.0, 2.0, 6.0)
                )

                if(horses.size == 0) {
                    error("Where's your ride?")
                    return
                }

                val ride = horses[0]

                RollBackCommand.instance?.runCommand("rollback", emptyArray())

                //canceler

                UseCommand.instance?.runCommand("use", arrayOf(ride.getEntityId() as String))

                state = true
            }
        }
    }

    fun packetReceived(
        direction : EnumPacketDirection,
        id : Int,
        packet : Packet<*>,
        buff : ByteBuf
    ) : Packet<*>? {
        if(state) {
            if(toCancel.contains(id)) {
                return null
            }
        }
        return packet
    }

    override fun getDescription(): String {
        return "Dupe from FFP"
    }

    override fun getSyntax(): String {
        return "rdupe reset or nothing"
    }
}