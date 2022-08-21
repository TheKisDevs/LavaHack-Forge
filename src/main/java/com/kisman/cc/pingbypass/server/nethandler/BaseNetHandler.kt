package com.kisman.cc.pingbypass.server.nethandler

import com.kisman.cc.util.TimerUtils
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketDisconnect
import net.minecraft.util.ITickable
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import org.apache.logging.log4j.LogManager

/**
 * @author _kisman_
 * @since 15:51 of 20.08.2022
 */
open class BaseNetHandler(
    protected val manager : NetworkManager,
    private val timeOut : Long
) : ITickable {
    protected val timer = TimerUtils()

    override fun update() {
        if(timer.passedMillis(timeOut)) {
            timer.reset()
            disconnect(TextComponentString("Time Out"))
        }
    }

    private fun disconnect(
        reason : ITextComponent
    ) {
        try {
            LOGGER.error("Disconnecting ${getConnectionInfo()} ${reason.formattedText}")

            manager.sendPacket(SPacketDisconnect(reason))
            manager.closeChannel(reason)
        } catch(e : Exception) {
            LOGGER.error("Error whilst disconnecting player", e)
        }
    }

    fun getConnectionInfo() : String = ""

    companion object {
        @JvmStatic val LOGGER = LogManager.getLogger()
    }
}