package com.kisman.cc.pingbypass.server.gui

import com.kisman.cc.Kisman
import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.Button
import com.kisman.cc.pingbypass.server.features.modules.PingBypassCategory
import com.kisman.cc.pingbypass.server.features.modules.PingBypassModuleManager

/**
 * @author _kisman_
 * @since 22:40 of 23.08.2022
 */
class PingBypassFrame(
    cat : PingBypassCategory,
    x : Int,
    y : Int
) : Frame(
    cat.category,
    x,
    y,
    true
) {
    init {
        var offsetY = HalqGui.height

        for ((count1, mod) in PingBypassModuleManager.getModulesByCategory(cat).withIndex()) {
            mods.add(Button(mod, x, y, offsetY, count1))
            offsetY += HalqGui.height
        }
    }
}