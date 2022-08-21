package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.movement.active
import com.kisman.cc.util.movement.stop

/**
 * @author _kisman_
 * @since 19:35 of 19.08.2022
 */
class PauseBaritone  : Module(
    "PauseBaritone",
    "Pauses current baritone process.",
    Category.CLIENT
) {
    override fun onEnable() {
        if(mc.player != null && mc.world != null) {
            if (active()) {
                stop()
                ChatUtility.complete().printClientModuleMessage("Successfully stopped baritone!")
            } else {
                ChatUtility.error().printClientModuleMessage("Baritone have any active processes!")
            }
        }

        super.setToggled(false)
    }
}