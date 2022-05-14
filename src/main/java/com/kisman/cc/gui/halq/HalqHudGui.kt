package com.kisman.cc.gui.halq

import com.kisman.cc.gui.MainGui

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class HalqHudGui : HalqGui(
        true
) {
    init {
        super.frames.add(Frame(15, 15))
    }

    override fun gui() : MainGui.Guis {
        return MainGui.Guis.HudEditor
    }
}