package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInstance
import com.kisman.cc.util.chat.cubic.ChatUtility

/**
 * @author _kisman_
 * @since 17:57 of 03.10.2022
 */
class ModuleInstancingKt : Module(
    "ModuleInstancingKt",
    "Tests @ModuleInstance annotation in kotlin.",
    Category.DEBUG
) {
    companion object {
        @ModuleInstance @JvmStatic var instance : ModuleInstancingKt? = null
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        ChatUtility.message().printClientModuleMessage("Instance of module $name is ${if(instance == null) "NULL" else "NOT NULL"}")
    }
}