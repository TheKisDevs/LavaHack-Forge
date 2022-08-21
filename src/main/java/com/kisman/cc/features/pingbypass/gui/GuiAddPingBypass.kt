package com.kisman.cc.features.pingbypass.gui

import com.kisman.cc.features.module.client.PingBypass
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard
import java.io.IOException

class GuiAddPingBypass(parentScreenIn: GuiScreen) : GuiScreen() {
    private val parentScreen: GuiScreen
    private var serverPortField: GuiTextField? = null
    private var serverIPField: GuiTextField? = null

    init {
        parentScreen = parentScreenIn
    }

    override fun updateScreen() {
        serverIPField?.updateCursorCounter()
        serverPortField?.updateCursorCounter()
    }

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        buttonList.clear()
        buttonList.add(GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, "Done"))
        buttonList.add(GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, "Cancel"))
        serverIPField = GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20)
        serverIPField!!.isFocused = true
        serverIPField!!.text = PingBypass.ip
        serverPortField = GuiTextField(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20)
        serverPortField!!.maxStringLength = 128
        serverPortField!!.text = PingBypass.port
        buttonList[0].enabled = serverPortField!!.text.isNotEmpty() && serverPortField!!.text.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().isNotEmpty() && serverIPField!!.text.isNotEmpty()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    @Throws(IOException::class)
    override fun keyTyped(
        typedChar : Char,
        keyCode : Int
    ) {
        serverIPField?.textboxKeyTyped(typedChar, keyCode)
        serverPortField?.textboxKeyTyped(typedChar, keyCode)
        if (keyCode == 15) {
            serverIPField?.isFocused = !serverIPField?.isFocused!!
            serverPortField?.isFocused = !serverPortField?.isFocused!!
        }
        if (keyCode == 28 || keyCode == 156) {
            actionPerformed(buttonList[0])
        }
        if (keyCode == 1) {
            mc.displayGuiScreen(parentScreen)
        }
        buttonList[0].enabled = serverPortField?.text?.isNotEmpty()!! && serverPortField?.text?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
            ?.isNotEmpty()!! && serverIPField?.text?.isNotEmpty()!!
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button.enabled) {
            if (button.id == 1) {
                parentScreen.confirmClicked(false, 1337)
            } else if (button.id == 0) {
                PingBypass.ip = serverIPField?.text!!
                PingBypass.port = serverPortField?.text!!
                parentScreen.confirmClicked(true, 1337)
            }
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        mouseButton : Int
    ) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        serverPortField?.mouseClicked(mouseX, mouseY, mouseButton)
        serverIPField?.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int,
        partialTicks : Float
    ) {
        drawDefaultBackground()
        drawCenteredString(fontRenderer, "Edit PingBypass", width / 2, 17, 16777215)
        drawString(fontRenderer, "Proxy-IP", width / 2 - 100, 53, 10526880)
        drawString(fontRenderer, "Proxy-Port", width / 2 - 100, 94, 10526880)
        serverIPField?.drawTextBox()
        serverPortField?.drawTextBox()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}