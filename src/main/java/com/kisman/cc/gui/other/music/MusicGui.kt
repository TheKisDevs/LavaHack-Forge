package com.kisman.cc.gui.other.music

import com.kisman.cc.Kisman
import com.kisman.cc.gui.KismanGuiScreen
import com.kisman.cc.gui.MainGui
import com.kisman.cc.gui.selectionbar.SelectionBar
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.net.music.Player
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import org.lwjgl.opengl.GL11

/**
 * @author _kisman_
 * @since 12:18 of 21.05.2022
 */
class MusicGui : KismanGuiScreen() {
    private var width_ = 100

    private var y_ = height / 6 + 48 - 6

    var field = GuiTextField(99, Minecraft.getMinecraft().fontRenderer, width / 2 - width_, y_, 200, 20)
    var offset = 72 - 48

    private val responder = VolumeResponder()
    private val formatter = VolumeFormatter()

    private var title : String? = null
    
    private var currentSong : String? = null

    private fun addButtons(y : Int, offset : Int) {
        val x = width / 2 - width_
        field.x = width / 2 - width_
        field.maxStringLength = 500
        field.width = 200
        buttonList.add(GuiButton(1, x, y + offset * 2, "Play/Stop"))
        buttonList.add(GuiButton(2, x, y + offset * 3, "Pause/Resume"))
        buttonList.add(GuiSlider(responder, 3, x, y + offset * 4, "Volume", 0f, 100f, 50f, formatter))
    }

    override fun initGui() {
        super.initGui()
        addButtons(y_, offset)
    }

    override fun drawScreen(mouseX : Int, mouseY : Int, ticks : Float) {
        if(Kisman.instance.selectionBar.selection != SelectionBar.Guis.Music) {
            MainGui.openGui(Kisman.instance.selectionBar)
            return
        }

        drawDefaultBackground()
        Kisman.instance.guiGradient.drawScreen(mouseX, mouseY)
        super.drawScreen(mouseX, mouseY, ticks)
        field.drawTextBox()
        GL11.glPushMatrix()
        GL11.glScaled(2.0, 2.0, 2.0)
        CustomFontUtil.drawCenteredStringWithShadow("Music", width / 4.0, 6.0, ColorUtils.astolfoColors(100, 100))
        GL11.glPopMatrix()

        if(currentSong == null) {
            title = null
        } else {
            title = "Playing $currentSong"
//            Thread {
//                println("88888")
//                val api = HitMotopAPI.getAPIByMP3Link(Player.currentSong) ?: return@Thread
//
//                title = "${api.findName()} by ${api.findAuthor()}"
//                println(title)
//            }.start()
        }

        if(title != null) {
            mc.fontRenderer.drawStringWithShadow(title!!,
                (width / 2 - mc.fontRenderer.getStringWidth(title!!) / 2).toFloat(),
                (6 + mc.fontRenderer.FONT_HEIGHT * 2 + 6).toFloat(), ColorUtils.astolfoColors(100, 100))
        }

        Player.setVolume(responder.volume.toFloat())

//        Kisman.instance.selectionBar.drawScreen(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
//        Kisman.instance.selectionBar.mouseClicked(mouseX, mouseY)
        super.mouseClicked(mouseX, mouseY, mouseButton)
        field.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        field.textboxKeyTyped(typedChar, keyCode)
    }

    override fun actionPerformed(button: GuiButton) {
        super.actionPerformed(button)
        when(button.id) {
            1 -> {
                if(Player.isPlaying) {
                    Player.stop()
                    currentSong = null
                } else {
                    try {
                        Player.play(field.text)
                        currentSong = field.text
                    } catch(e : Exception) {
                        //TODO
                    }
                }
            }
            2 -> {
                try {
                    if (Player.isPlaying) {
                        Player.pause()
                    } else {
                        Player.resume()
                    }
                } catch(e : Exception) {
                    //TODO
                }
            }
        }
    }

    class VolumeResponder : GuiPageButtonList.GuiResponder {
        var volume : Int = 100

        override fun setEntryValue(p0: Int, p1: Float) {
            volume = p1.toInt()
        }

        override fun setEntryValue(p0: Int, p1: Boolean) {}
        override fun setEntryValue(p0: Int, p1: String) {}
    }

    class VolumeFormatter : GuiSlider.FormatHelper {
        override fun getText(id : Int, name : String, value: Float): String {
            return "$name: ${value.toInt()}%"
        }
    }
}