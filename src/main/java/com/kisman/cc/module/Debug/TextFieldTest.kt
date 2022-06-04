package com.kisman.cc.module.Debug

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.util.render.gui.TextFieldHandler
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen

/**
 * @author _kisman_
 * @since 15:12 of 28.05.2022
 */
class TextFieldTest : Module(
    "TextFieldTest",
    "Test of GuiTextField handler",
    Category.DEBUG
) {
    val gui = TestGui()

    override fun onEnable() {
        mc.displayGuiScreen(gui)
        toggled = false
    }

    class TestGui : GuiScreen() {
        val field = TextFieldHandler(
            100,
            100,
            22,
            200
        )

        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            drawDefaultBackground()
            field.drawTextBox()
        }

        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            field.mouseClicked(mouseX, mouseY, mouseButton)
        }

        override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
//            field.mouse
        }

        override fun actionPerformed(button: GuiButton) {
            super.actionPerformed(button)
        }

        override fun initGui() {
            field.init()
        }

        override fun doesGuiPauseGame(): Boolean {
            return true;
        }

        override fun keyTyped(typedChar: Char, keyCode: Int) {
            if(keyCode == 1) {
                mc.currentScreen = null
            }
        }
    }
}