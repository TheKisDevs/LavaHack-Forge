package com.kisman.cc.gui.hudeditor

import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.selectionbar.SelectionBar
import com.kisman.cc.util.Colour

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class HalqHudGui : HalqGui(
        true
) {
    var color = Colour(10, 10, 10, 170)

    val settingsFrame : HudSettingsFrame

    init {
        var offsetX = 15

        super.frames.add(Frame(15, 15, "Hud Editor"))

        offsetX += HalqGui.width + 15

        settingsFrame = HudSettingsFrame(
            offsetX,
            15
        )

        super.frames.add(settingsFrame)
    }

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int,
        partialTicks : Float
    ) {
        color = settingsFrame.colorSetting.colour

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun gui() : SelectionBar.Guis {
        return SelectionBar.Guis.HudEditor
    }
}