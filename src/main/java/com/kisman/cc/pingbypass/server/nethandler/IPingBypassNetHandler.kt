package com.kisman.cc.pingbypass.server.nethandler

import com.kisman.cc.pingbypass.server.PingBypassServer
import com.kisman.cc.pingbypass.utils.disconnectFromMC
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.util.text.ITextComponent
import org.apache.logging.log4j.LogManager

/**
 * @author _kisman_
 * @since 18:59 of 20.08.2022
 */
interface IPingBypassNetHandler : INetHandlerPlayServer  {
    companion object {
        val LOGGER = LogManager.getLogger()
    }

    fun handle(
        packet : Packet<*>
    ) {}

    override fun onDisconnect(
        reason : ITextComponent
    ) {
        LOGGER.info("Quitting: ${reason.unformattedText}")

        if(!PingBypassServer.stay) {
            disconnectFromMC("PingBypass Client has disconnected.")
        }

        PingBypassServer.connected = false
        PingBypassServer.stay = false
    }

    override fun handleAnimation(
        packet : CPacketAnimation
    ) {
        handle(packet)
    }

    override fun processChatMessage(
        packet : CPacketChatMessage
    ) {
        handle(packet)
    }

    override fun processTabComplete(
        packet : CPacketTabComplete
    ) {
        handle(packet)
    }

    override fun processClientStatus(
        packet : CPacketClientStatus
    ) {
        handle(packet)
    }

    override fun processClientSettings(
        packet : CPacketClientSettings
    ) {
        handle(packet)
    }

    override fun processConfirmTransaction(
        packet : CPacketConfirmTransaction
    ) {
        handle(packet)
    }

    override fun processEnchantItem(
        packet : CPacketEnchantItem
    ) {
        handle(packet)
    }

    override fun processClickWindow(
        packet : CPacketClickWindow
    ) {
        handle(packet)
    }

    override fun func_194308_a(
        packet : CPacketPlaceRecipe
    ) {
        handle(packet)
    }

    override fun processCloseWindow(
        packet : CPacketCloseWindow
    ) {
        handle(packet)
    }

    override fun processCustomPayload(
        packet : CPacketCustomPayload
    ) {
        handle(packet)
    }

    override fun processUseEntity(
        packet : CPacketUseEntity
    ) {
        handle(packet)
    }

    override fun processKeepAlive(
        packet : CPacketKeepAlive
    ) {
        handle(packet)
    }

    override fun processPlayer(
        packet : CPacketPlayer
    ) {
        handle(packet)
    }

    override fun processPlayerAbilities(
        packet : CPacketPlayerAbilities
    ) {
        handle(packet)
    }

    override fun processPlayerDigging(
        packet : CPacketPlayerDigging
    ) {
        handle(packet)
    }

    override fun processEntityAction(
        packet : CPacketEntityAction
    ) {
        handle(packet)
    }

    override fun processInput(
        packet : CPacketInput
    ) {
        handle(packet)
    }

    override fun processHeldItemChange(
        packet : CPacketHeldItemChange
    ) {
        handle(packet)
    }

    override fun processCreativeInventoryAction(
        packet : CPacketCreativeInventoryAction
    ) {
        handle(packet)
    }

    override fun processUpdateSign(
        packet : CPacketUpdateSign
    ) {
        handle(packet)
    }

    override fun processTryUseItemOnBlock(
        packet : CPacketPlayerTryUseItemOnBlock
    ) {
        handle(packet)
    }

    override fun processTryUseItem(
        packet : CPacketPlayerTryUseItem
    ) {
        handle(packet)
    }

    override fun handleSpectate(
        packet : CPacketSpectate
    ) {
        handle(packet)
    }

    override fun handleResourcePackStatus(
        packet : CPacketResourcePackStatus
    ) {
        handle(packet)
    }

    override fun processSteerBoat(
        packet : CPacketSteerBoat
    ) {
        handle(packet)
    }

    override fun processVehicleMove(
        packet : CPacketVehicleMove
    ) {
        handle(packet)
    }

    override fun processConfirmTeleport(
        packet : CPacketConfirmTeleport
    ) {
        handle(packet)
    }

    override fun handleRecipeBookUpdate(
        packet : CPacketRecipeInfo
    ) {
        handle(packet)
    }

    override fun handleSeenAdvancements(
        packet : CPacketSeenAdvancements
    ) {
        handle(packet)
    }
}