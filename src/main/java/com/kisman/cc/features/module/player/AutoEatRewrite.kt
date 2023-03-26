package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import net.minecraft.item.ItemFood

/**
 * @author _kisman_
 * @since 20:12 of 08.03.2023
 */
@ModuleInfo(
    name = "AutoEatRewrite",
    display = "AutoEat",
    desc = "Will automatically eat",
    category = Category.PLAYER
)
class AutoEatRewrite : Module() {
    override fun onDisable() {
        super.onDisable()

        if(mc.player == null || mc.world == null) {
            return
        }

        mc.gameSettings.keyBindUseItem.pressed = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(mc.player.heldItemMainhand.item is ItemFood) {
            mc.gameSettings.keyBindUseItem.pressed = true
        } else if(mc.gameSettings.keyBindUseItem.pressed) {
            mc.gameSettings.keyBindUseItem.pressed = false
        }
    }
}