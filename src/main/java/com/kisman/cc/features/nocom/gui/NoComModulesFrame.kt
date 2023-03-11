package com.kisman.cc.features.nocom.gui

import com.kisman.cc.Kisman
import com.kisman.cc.features.nocom.NoComModuleManager
import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.Button

/**
 * @author _kisman_
 * @since 12:17 of 28.08.2022
 */
class NoComModulesFrame(
    x : Int,
    y : Int
) : Frame(
    null,
    x,
    y,
    true,
    "Modules"
) {
    init {
        for((i, module) in Kisman.instance.noComModuleManager.modules.withIndex()) {
            components.add(Button(
                module,
                x,
                y,
                (i + 1) * HalqGui.height,
                i + 1,
                0
            ))
        }
    }
}