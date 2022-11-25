package com.kisman.cc.gui

import com.kisman.cc.Kisman
import net.minecraft.client.gui.GuiScreen

/**
 * @author _kisman_
 * @since 22:10 of 24.11.2022
 */
open class KismanGuiScreen : GuiScreen() {
    override fun initGui() {
        super.initGui()

        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.initGui()
        }
    }

    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        mouseButton : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.mouseClicked(mouseButton, mouseX, mouseY)
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    fun drawSelectionBar(
        mouseX : Int,
        mouseY : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.drawScreen(mouseX, mouseY)
        }
    }

    override fun keyTyped(
        typedChar : Char,
        keyCode : Int
    ) {
        if(Kisman.instance.selectionBar.isValid()) {
            Kisman.instance.selectionBar.keyTyped(typedChar, keyCode)
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int,
        partialTicks : Float
    ) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        drawSelectionBar(mouseX, mouseY)
    }
}