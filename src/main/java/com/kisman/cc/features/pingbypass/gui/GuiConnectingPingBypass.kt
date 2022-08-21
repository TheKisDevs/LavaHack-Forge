package com.kisman.cc.features.pingbypass.gui

import com.kisman.cc.features.module.client.PingBypass
import com.kisman.cc.features.pingbypass.utility.getDots
import com.kisman.cc.util.enums.PingBypassProtocol
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerAddress
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.network.NetHandlerLoginClient
import net.minecraft.client.resources.I18n
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.CPacketLoginStart
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger

class GuiConnectingPingBypass(
    parent : GuiScreen,
    mcIn : Minecraft,
    serverDataIn : ServerData
) : GuiScreen() {
    private var networkManager : NetworkManager? = null
    private var cancel = false
    private val previousGuiScreen : GuiScreen

    init {
        this.mc = mcIn
        previousGuiScreen = parent
        mcIn.loadWorld(null)
        mcIn.setServerData(serverDataIn)
        val serveraddress = ServerAddress.fromString(serverDataIn.serverIP)

        connect(
            PingBypass.ip,
            PingBypass.portInt,
            serveraddress.ip,
            serveraddress.port
        )
    }

    private fun connect(
        proxyIP : String,
        proxyPort : Int,
        actualIP : String,
        actualPort : Int
    ) {
        LOGGER.info("Connecting to PingBypass: {}, {}", proxyIP, proxyPort)
        object : Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            override fun run() {
                var inetaddress : InetAddress? = null
                try {
                    if (cancel) {
                        return
                    }
                    inetaddress = InetAddress.getByName(proxyIP)
                    networkManager = NetworkManager
                        .createNetworkManagerAndConnect(
                            inetaddress!!,
                            proxyPort,
                            mc.gameSettings.isUsingNativeTransport
                        )
                    networkManager?.netHandler = NetHandlerLoginClient(networkManager!!, mc, previousGuiScreen)

                    if(PingBypass.protocol.valEnum == PingBypassProtocol.Legacy || (proxyIP == actualIP && proxyPort == actualPort)) {
                        networkManager?.sendPacket(
                            C00Handshake(actualIP, actualPort, EnumConnectionState.LOGIN)
                        )
                    } else {
                        networkManager?.sendPacket(
                            C00Handshake(actualIP, actualPort, EnumConnectionState.PLAY)
                        )
                    }
                    networkManager?.sendPacket(
                        CPacketLoginStart(mc.getSession().profile)
                    )
                } catch (e : UnknownHostException) {
                    if (cancel) {
                        return
                    }
                    LOGGER.error("Couldn't connect to PingBypass", e)
                    mc.addScheduledTask {
                        mc.displayGuiScreen(
                            GuiDisconnected(
                                previousGuiScreen,
                                "connect.failed",
                                TextComponentTranslation("disconnect.genericReason", "Unknown host")
                            )
                        )
                    }
                } catch (exception: Exception) {
                    if (cancel) {
                        return
                    }
                    LOGGER.error("Couldn't connect to PingBypass", exception)
                    var s = exception.toString()
                    if (inetaddress != null) {
                        val s1 = "$inetaddress:$proxyPort"
                        s = s.replace(s1.toRegex(), "")
                    }
                    val finalS = s
                    mc.addScheduledTask {
                        mc.displayGuiScreen(
                            GuiDisconnected(
                                previousGuiScreen,
                                "connect.failed",
                                TextComponentTranslation("disconnect.genericReason", finalS)
                            )
                        )
                    }
                }
            }
        }.start()
    }

    override fun updateScreen() {
        if (networkManager != null) {
            if (networkManager?.isChannelOpen!!) {
                networkManager?.processReceivedPackets()
            } else {
//                networkManager?.checkDisconnected()
            }
        }
    }

    override fun initGui() {
        this.buttonList.clear()
        this.buttonList.add(GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")))
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            cancel = true
            if (networkManager != null) {
                networkManager?.closeChannel(TextComponentString("Aborted"))
            }
            this.mc.displayGuiScreen(previousGuiScreen)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        if (networkManager == null) {
            this.drawCenteredString(
                this.fontRenderer,
                "Authentication" + getDots(),
                this.width / 2 + getDots().length,
                this.height / 2 - 50,
                16777215
            )
        } else {
            this.drawCenteredString(
                this.fontRenderer,
                "Loading PingBypass" + getDots(),
                this.width / 2 + getDots().length,
                this.height / 2 - 50,
                16777215
            )
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    companion object {
        private val CONNECTION_ID = AtomicInteger(0)
        private val LOGGER = LogManager.getLogger()
    }
}