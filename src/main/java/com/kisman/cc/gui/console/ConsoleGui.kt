package com.kisman.cc.gui.console

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.console.ConsoleMessageEvent
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.gui.KismanGuiScreen
import com.kisman.cc.gui.api.Draggable
import com.kisman.cc.gui.selectionbar.SelectionBar
import com.kisman.cc.util.fix
import com.mojang.realmsclient.gui.ChatFormatting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.UnsupportedEncodingException

/**@author Dallas/gerald0mc
 */
class ConsoleGui : KismanGuiScreen(), Draggable {
    private var width1 : Int = 300
    private var height1 : Int = 250 - 25
    private var x : Int = 25
    private var y : Int = 25

    private val history = ArrayList<String>()
    var entryString = ""

    private var drag = false
    private var dragX = 0
    private var dragY = 0

    @EventHandler val onMessage = Listener(EventHook { event: ConsoleMessageEvent ->
        history.add(event.message)
    })

    init {
        Kisman.EVENT_BUS.subscribe(onMessage)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(Kisman.instance.selectionBar.selection != SelectionBar.Guis.Console) {
            Kisman.instance.selectionBar.open()
            return
        }

        super.drawDefaultBackground()
        super.drawScreenPre()
        super.drawScreen(mouseX, mouseY, partialTicks)

        if(drag) {
            x = mouseX - dragX
            y = mouseY - dragY
        }

        fix(this)

        if(history.size >= 25) {
            history.removeAt(0)
        }

        width1 = if(getLongestWord(history) > 300)  getLongestWord(history) + 3 else 300

        //Full box
        drawRect(x, y, x + width1, y + height1, Color(0, 0, 0, 175).rgb)

        //Lines
        if(Config.instance.guiOutline.valBoolean) {
            //Top line
            drawRect(x - 1, y, x + width1, y + 1, Color.BLACK.rgb)
            //Left line
            drawRect(x - 1, y, x, height1, Color.BLACK.rgb)
            //Right line
            drawRect(x - 1 + width1, y, x + width1, height1, Color.BLACK.rgb)
            //Bottom line
            drawRect(x - 1, y + height1, x + width1, y + height1 + 1, Color.BLACK.rgb)
        }

        var yOffset = 0

        for(string in history) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string,
                (x + 2).toFloat(), (y + 2 + yOffset).toFloat(), -1)
            yOffset += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
        }

        //Entry string
        fontRenderer.drawStringWithShadow("${entryString}_", (x + 2).toFloat(),
            (y + height1 - fontRenderer.FONT_HEIGHT).toFloat(), -1)

//        Kisman.instance.selectionBar.drawScreen(mouseX, mouseY)
        drawSelectionBar(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if(Mouse.isButtonDown(0) && mouseX >= x && mouseX <= x + width1 && mouseY >= y && mouseY <= y + height1) {
            drag = true
            dragX = mouseX - x
            dragY = mouseY - y
        }
//        Kisman.instance.selectionBar.mouseClicked(mouseX, mouseY)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        drag = false
    }

    public override fun keyTyped(typedChar: Char, keyCode: Int) {
        if(keyCode == 1) {
            Minecraft.getMinecraft().displayGuiScreen(null)
            return
        }
        when(keyCode) {
            Keyboard.KEY_BACK -> entryString = removeLastLetter(entryString)
            Keyboard.KEY_RETURN -> {
                when (entryString.toLowerCase()) {
                    "clear" -> {
                        history.clear()
                        history += "Cleared console!"
                    }
                    else -> {
                        if (entryString.isNotEmpty()) {
                            history += "> $entryString"
                        }
                        Kisman.instance.commandManager.runCommands("-${entryString}")
                    }
                }
                entryString = ""
            }
            Keyboard.KEY_V ->
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    try {
                        entryString += Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor)
                    } catch (ignored: UnsupportedEncodingException) {}
                }
            Keyboard.KEY_C ->
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    if(entryString.isEmpty()) {
                        history.add("${ChatFormatting.BOLD}[Console]${ChatFormatting.RESET} Nothing to copy.")
                        return
                    }
                    try {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(entryString), null)
                        history += "${ChatFormatting.BOLD}[Console]${ChatFormatting.RESET} Copied text in string box to clipboard."
                    } catch(ignored: IllegalStateException) {}
                }
        }
        if(ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            entryString += typedChar
            super.keyTyped(typedChar, keyCode)
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    private fun removeLastLetter(string: String?): String {
        var out = ""
        if (string != null && string.isNotEmpty()) {
            out = string.substring(0, string.length - 1)
        }
        return out
    }

    private fun getLongestWord(strings: List<String>): Int {
        var max = 0

        for(string in strings) {
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(string) > max) max = Minecraft.getMinecraft().fontRenderer.getStringWidth(string)
        }
        return max
    }

    override fun onGuiClosed() {
        try {
            if (mc.player != null && mc.world != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup()
            }
        } catch (ignored: Exception) { }
        super.onGuiClosed()
    }

    override fun getX(): Double { return x.toDouble() }
    override fun getY(): Double { return y.toDouble() }
    override fun getW(): Double { return width1.toDouble() }
    override fun getH(): Double { return height1.toDouble() }
    override fun setX(x: Double) { this.x = x.toInt() }
    override fun setY(y: Double) { this.y = y.toInt() }
    override fun setW(w: Double) { this.width1 = w.toInt() }
    override fun setH(h: Double) { this.height1 = h.toInt() }
}