package com.kisman.cc.features.module.Debug.submoduletest

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.chat.cubic.ChatUtility

/**
 * @author _kisman_
 * @since 12:37 of 06.03.2023
 */
@ModuleInfo(
    name = "SubModule1",
    desc = "Description of SubModule1",
    submodule = true
)
class SubModule1 : Module() {
    private val setting = register(Setting("Setting lol", this, true))

    private val group = register(SettingGroup(Setting("Group lmao", this)))

    private val setting2 = register(group.add(Setting("Setting xd", this, false)))

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null) {
            return
        }

        ChatUtility.info().printClientModuleMessage("onEnable!", 98)
    }

    override fun onDisable() {
        super.onDisable()

        if(mc.player == null || mc.world == null) {
            return
        }

        ChatUtility.info().printClientModuleMessage("onDisable!", 100)
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        ChatUtility.info().printClientModuleMessage("update!", 99)
    }
}