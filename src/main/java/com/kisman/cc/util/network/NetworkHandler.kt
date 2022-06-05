package com.kisman.cc.util.network

import com.kisman.cc.features.command.commands.RollBackDupeCommand
import com.kisman.cc.features.module.Debug.PacketIDGetterTest
import io.netty.buffer.ByteBuf
import net.minecraft.network.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent


/**
 * @author _kisman_
 * @since 15:31 of 04.06.2022
 */
class NetworkHandler {
    private var networkManager : NetworkManager? = null
    private var isConnected = false

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun packetReceived(
        direction : EnumPacketDirection,
        id : Int,
        packet : Packet<*>,
        buff : ByteBuf?
    ) : Packet<*>? {
        //TODO make custom event
        var packet1: Packet<*>? = (if(RollBackDupeCommand.instance != null) RollBackDupeCommand.instance?.packetReceived(direction, id, packet, buff)!! else packet)
        packet1 = (if(PacketIDGetterTest.instance != null) PacketIDGetterTest.instance?.packetReceived(direction, id, packet, buff)!! else packet)
        return packet1;
    }

    @SubscribeEvent fun onConnect(event : FMLNetworkEvent.ClientConnectedToServerEvent) {
        val pipeline = event.manager.channel().pipeline()

        try {
            // Install receive interception
            var old = pipeline["decoder"]
            if (old != null && old is NettyPacketDecoder) {
                val spoof = InboundInterceptor(this, EnumPacketDirection.CLIENTBOUND)
                pipeline.replace("decoder", "decoder", spoof)
            }

            // Install send interception
            old = pipeline["encoder"]
            if (old != null && old is NettyPacketEncoder) {
                val spoof = OutboundInterceptor(this, EnumPacketDirection.SERVERBOUND)
                pipeline.replace("encoder", "encoder", spoof)
            }

            // Install special frame encoder
            old = pipeline["prepender"]
            if (old != null && old is NettyVarint21FrameEncoder) {
                val spoof = OutboundFrameEncoder()
                pipeline.replace("prepender", "prepender", spoof)
            }

            // Record NetworkManager
            this.networkManager = event.manager
            this.isConnected = true
        } catch (e : NoSuchElementException) { }
    }

    @SubscribeEvent fun onDisconnect(event : FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        isConnected = false
    }
}