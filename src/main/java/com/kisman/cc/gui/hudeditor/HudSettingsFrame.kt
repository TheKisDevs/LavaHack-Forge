package com.kisman.cc.gui.hudeditor

import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.sub.ColorButton
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour

/**
 * @author _kisman_
 * @since 21:11 of 26.08.2022
 */
class HudSettingsFrame(
    x : Int,
    y : Int
) : Frame(
    null,
    x,
    y,
    true,
    "Settings"
) {
    val colorSetting = Setting("Box Color", null, Colour(10, 10, 10, 170))

    init {
        var offsetY = HalqGui.height
        var count1 = 0

        components.add(ColorButton(
            colorSetting,
            x,
            y,
            offsetY,
            count1,
            0
        ))

        offsetY += HalqGui.height
        count1++
    }
}