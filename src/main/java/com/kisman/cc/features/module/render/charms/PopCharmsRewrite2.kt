package com.kisman.cc.features.module.render.charms

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.render.charms.popcharms.EntityPopped
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.copy
import com.kisman.cc.util.enums.PopCharmsDirections
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.string2int
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
    private val easing = register(SettingEnum("Easing", this, EasingEnum.Easing.Linear))
    private val groups = register(SettingsList.Groups("model", SettingGroup(Setting("Model", this)), "wire", SettingGroup(Setting("Wire", this))))
    private val fade = register(
        register(SettingGroup(Setting("Fade", this))).addAll(groups["model"], groups["wire"]).add(
            SettingsList(
                "state", Setting("Fade State", this, false).setTitle("State"),
                "model", groups["model"].add(Setting("Fade Model When", this, 1.0, 0.0, 1.0, false).setTitle("When")),
                "model easing", groups["model"].add(SettingArray("Fade Model Easing", this, EasingEnum.EasingReverse.Linear, EasingEnum.allEasingsReverse).setTitle("Easing")),
                "wire", groups["wire"].add(Setting("Wire Model When", this, 1.0, 0.0, 1.0, false).setTitle("When")),
                "wire easing", groups["wire"].add(SettingArray("Fade Wire Easing", this, EasingEnum.EasingReverse.Linear, EasingEnum.allEasingsReverse).setTitle("Easing"))
            )
        )
    )

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
                EntityPopped(
                    mc.world,
                    player.gameProfile,
                    player,
                    moduleId + string2int(player.name) + Random().nextInt(),
                    direction.valEnum.facing(),
                    speed.valDouble,
                    Triple(
                        length.valLong,
                        (fade["model"].valFloat * length.valLong).toLong(),
                        (fade["wire"].valFloat * length.valLong).toLong()),
                    Triple(
                        easing.valEnum,
                        fade.get0<SettingArray<EasingEnum.IEasing>>("model easing").valElement,
                        fade.get0<SettingArray<EasingEnum.IEasing>>("wire easing").valElement
                    )
                ).also { it1 ->
                    copy(player, it1)
                }
            }
        }
    })
}