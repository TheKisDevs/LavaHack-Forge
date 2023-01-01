package com.kisman.cc.features.viaforge.gui

import com.kisman.cc.Kisman
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.halq.Frame
import com.kisman.cc.gui.halq.HalqGui
import net.minecraft.client.gui.GuiScreen

/**
 * @author _kisman_
 * @since 17:37 of 01.01.2023
 */

@JvmField var component : Component? = null

class ViaForgeGui : HalqGui(
    true
) {
    init {
        frames.add(MainFrame())
    }

    override fun setLastGui(
        gui : GuiScreen
    ) : ViaForgeGui = super.setLastGui(gui) as ViaForgeGui
}

class MainFrame : Frame(
    null,
    10,
    10,
    true,
    "ViaForge"
) {
    init {
        if(component != null) {
            components.add(component)
        } else {
            Kisman.LOGGER.error("ViaForge module component is null!")
        }
    }
}