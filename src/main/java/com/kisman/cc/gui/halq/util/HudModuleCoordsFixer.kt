package com.kisman.cc.gui.halq.util

import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.util.Globals
import net.minecraft.client.gui.ScaledResolution

/**
 * @author _kisman_
 * @since 22:09 of 20.06.2022
 */
class HudModuleCoordsFixer {
    companion object {
        fun fix(hud : HudModule) {
            if(hud.x < 0) {
                hud.x = 0.0
            }
            if(hud.y < 0) {
                hud.y = 0.0
            }


            val sr = ScaledResolution(Globals.mc)

            if(hud.x > sr.scaledWidth - hud.w) {
                hud.x = sr.scaledWidth - hud.w
            }
            if(hud.y > sr.scaledHeight - hud.h) {
                hud.y = sr.scaledHeight - hud.h
            }
        }
    }
}