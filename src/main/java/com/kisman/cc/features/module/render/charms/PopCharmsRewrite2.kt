package com.kisman.cc.features.module.render.charms

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.render.charms.popcharms.EntityPopped
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.StringUtils
import com.kisman.cc.util.enums.PopCharmsDirections
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketEntityStatus
import java.util.Random

/**
 * @author _kisman_
 * @since 8:37 of 24.03.2023
 */
@ModuleInfo(
    name = "PopCharmsRewrite2",
    display = "Pops",
    submodule = true
)
class PopCharmsRewrite2 : Module() {
    private val direction = register(SettingEnum("Direction", this, PopCharmsDirections.Up))
    private val speed = register(Setting("Speed", this, 0.01, 0.0, 1.0, false))
    private val length = register(Setting("Length", this, 1000.0, 100.0, 10000.0, NumberType.TIME))

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private val receive = Listener<PacketEvent.Receive>(EventHook { it0 ->
        val packet = it0.packet

        if(packet is SPacketEntityStatus && packet.opCode == 35.toByte()) {
            val player = packet.getEntity(mc.world)

            if(player is EntityPlayer) {
                EntityPopped(mc.world, player.gameProfile, player, moduleId + StringUtils.stringToInt(player.name) + Random().nextInt(), direction.valEnum.facing(), speed.valDouble, length.valLong).also { it1 ->
                    it1.copyLocationAndAnglesFrom(player)
                    it1.rotationYaw = player.rotationYaw
                    it1.rotationPitch = player.rotationPitch
                    it1.rotationYawHead = player.rotationYawHead
                    it1.renderYawOffset = player.renderYawOffset
                    it1.prevRotationYaw = player.prevRotationYaw
                    it1.prevRotationYawHead = player.prevRotationYawHead
                    it1.prevRenderYawOffset = player.prevRenderYawOffset
                    it1.setPositionAndRotationDirect(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, 0, false)
                    it1.health = 20f
                    it1.noClip = true
                    it1.onLivingUpdate()
                }
            }
        }
    })
}