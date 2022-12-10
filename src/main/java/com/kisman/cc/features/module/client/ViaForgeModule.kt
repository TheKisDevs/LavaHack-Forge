package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.settings.EventSettingChange
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.viaforge.ViaForge
import com.kisman.cc.features.viaforge.protocol.ProtocolCollection
import com.kisman.cc.settings.types.SettingEnum
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

/**
 * @author _kisman_
 * @since 21:39 of 03.12.2022
 */
class ViaForgeModule : Module(
    "ViaForge",
    "Implementation of viaforge version selector",
    Category.CLIENT
) {
    private val version = SettingEnum("Version", this, ProtocolCollection.R1_12_2).register()

    private var changed = false

    private val settingChange = Listener<EventSettingChange.ModeSetting>(EventHook {
        if(it.setting == version) {
            if(mc.world != null) {
                changed = true
            } else {
                ViaForge.getInstance().version = version.valEnum.version.version
            }
        }
    })

    init {
        super.setToggled(true)
        super.toggleable = false

        Kisman.EVENT_BUS.subscribe(settingChange)
    }

    override fun update() {
        if(mc.world == null && changed) {
            ViaForge.getInstance().version = version.valEnum.version.version
            changed = false
        }
    }
}