package com.kisman.cc.features.nocom.gui

import com.kisman.cc.gui.MainGui
import com.kisman.cc.gui.halq.HalqGui

/**
 * @author _kisman_
 * @since 0:39 of 28.08.2022
 */
class NoComGui : HalqGui(
    true
) {
    init {
        frames.add(NoComModulesFrame(
            15,
            15
        ))
    }

    override fun gui(): MainGui.Guis {
        return super.gui()
    }
}