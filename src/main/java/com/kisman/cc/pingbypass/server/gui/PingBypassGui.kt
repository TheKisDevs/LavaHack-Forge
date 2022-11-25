package com.kisman.cc.pingbypass.server.gui

import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.selectionbar.SelectionBar
import com.kisman.cc.pingbypass.server.features.modules.PingBypassCategory

/**
 * @author _kisman_
 * @since 22:00 of 23.08.2022
 */
class PingBypassGui : HalqGui(true) {
    init {
        var offsetX = 0
        for (cat in PingBypassCategory.values()) {
            frames.add(PingBypassFrame(cat, offsetX, 17))
            offsetX += headerOffset * 2 + HalqGui.width - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1
        }
    }

    override fun gui() : SelectionBar.Guis {
        return SelectionBar.Guis.PingBypassGui
    }
}
