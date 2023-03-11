package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.viaforge.ViaForge
import com.kisman.cc.features.viaforge.protocol.ProtocolCollection
import com.kisman.cc.settings.types.SettingEnum

/**
 * @author _kisman_
 * @since 21:39 of 03.12.2022
 */
@ModuleInfo(
    name = "ViaForge",
    desc = "Version selector of implementation of viaforge",
    category = Category.CLIENT,
    toggled = true,
    toggleable = false
)
class ViaForgeModule : Module() {
    private val version = register(SettingEnum("Version", this, ProtocolCollection.R1_12_2)
        .onChange0 { it : SettingEnum<ProtocolCollection> ->
            if (mc.world != null) {
                changed = true
            } else {
                ViaForge.getInstance().version = it.valEnum.version.version
            }
        }
    )

    private var changed = false

    override fun update() {
        if(mc.world == null && changed) {
            ViaForge.getInstance().version = version.valEnum.version.version
            changed = false
        }
    }
}