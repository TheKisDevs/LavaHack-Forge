package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.player.frecam.EntityFreeCam
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.copy
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketPlayer

/**
 * @author _kisman_
 * @since 19:37 of 18.04.2023
 */
@ModuleInfo(
    name = "FreeCamRewrite2",
    display = "FreeCam",
    category = Category.PLAYER
)
class FreeCamRewrite2 : Module() {
    private val speeds = register(register(SettingGroup(Setting("Speeds", this))).add(SettingsList("h", Setting("Speed H", this, 1.0, 0.1, 2.0, false).setTitle("H"), "v", Setting("Speed v", this, 1.0, 0.1, 2.0, false).setTitle("V"))))
    private val cancel = register(Setting("Cancel", this, false))

    private var entity : EntityFreeCam? = null

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(send)

        if(mc.player == null || mc.world == null) {
            return
        }

        EntityFreeCam(mc.world, mc.player.gameProfile, mc.player, -moduleId, speeds["h"], speeds["v"]).also {
            entity = it
            copy(mc.player, it)
            mc.renderManager.renderViewEntity = it
        }
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(send)

        if(mc.player == null || mc.world == null || entity == null) {
            return
        }

        mc.world.removeEntityFromWorld(-moduleId)
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        mc.player.motionX = 0.0
        mc.player.motionY = 0.0
        mc.player.motionZ = 0.0
        mc.player.movementInput.moveForward = 0f
        mc.player.movementInput.moveStrafe = 0f
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        val packet = it.packet

        if(packet is CPacketPlayer && cancel.valBoolean) {
            it.cancel()
        }
    })
}