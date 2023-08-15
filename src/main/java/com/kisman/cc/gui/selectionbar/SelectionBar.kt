package com.kisman.cc.gui.selectionbar

import com.kisman.cc.Kisman
import com.kisman.cc.gui.selectionbar.element.IElement
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

class SelectionBar(
        defaultSelection : Guis
) {
    var selection : Guis
    val backgroundColor : Colour = Colour(20, 20, 20, 200)
    val offset : Int = 5

    val additions = ArrayList<IElement>()

    init {
        selection = defaultSelection
    }

    var reinit = false

    fun open() {
        selection.open()
    }

    fun initGui() {
        reinit = true
    }

    fun drawScreen(mouseX : Int, mouseY : Int) {
        var startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
        Render2DUtil.drawRectWH(
            startX.toDouble(),
            0.0,
            getSelectionBarWidth().toDouble(),
            (CustomFontUtil.getFontHeight() + offset * 2).toDouble(),
            backgroundColor.rgb
        )

//        val xCache = HashMap<IElement, Int>()

        for(addition in additions) {
            if(reinit) {
                if(selection.additions0().contains(addition)) {
                    addition.init(startX, 0)
                }
            }

            addition.draw(startX, 0, mouseX, mouseY)
//            xCache[addition] = startX
            startX += addition.width()
        }

        reinit = false

        for(gui in Guis.values()) {
            CustomFontUtil.drawStringWithShadow(
                gui.displayName,
                (startX + offset).toDouble(),
                offset.toDouble(),
                if (gui == selection) ColorUtils.astolfoColors(100, 100) else -1
            )
            if(Mouse.isButtonDown(0)) {
                if(mouseX >= startX && mouseX <= startX + offset * 2 + CustomFontUtil.getStringWidth(gui.displayName) && mouseY >= 0 && mouseY <= offset * 2 + CustomFontUtil.getFontHeight()
                ) {
                    selection.close0()
                    selection = gui
                    selection.open0()

                    reinit = true

                    /*for(addition in additions) {
                        addition.init(startX, 0)
                    }*/
                }
            }
            startX += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
        }
    }

    fun mouseClicked(button : Int, mouseX : Int, mouseY : Int) : Boolean {
        for(addition in additions) {
            addition.mouse(button, mouseX, mouseY)
        }
        /*val startX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - getSelectionBarWidth() / 2
        if(mouseX >= startX && mouseX <= startX + getSelectionBarWidth() && mouseY >= 0 && mouseY <= CustomFontUtil.getFontHeight() + offset * 2) {
            for((count, gui) in Guis.values().withIndex()) {
                if(mouseX >= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) && mouseX <= startX + (count * (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) + (offset * 2 + CustomFontUtil.getStringWidth(gui.displayName))) {
                    println("Gui: ${gui.displayName}")
                    selection = gui
                    return false
                }
            }
        }
        return true*/
        return true
    }

    fun keyTyped(
        char : Char,
        key : Int
    ) {
        for(addition in additions) {
            addition.key(key, char)
        }
    }

    fun isValid() : Boolean = Minecraft.getMinecraft().currentScreen == Kisman.instance.selectionBar.selection.gui()

    private fun getSelectionBarWidth() : Int {
        var width = 0

        for(addition in additions) {
            width += addition.width()
        }

        for(gui in Guis.values()) {
            width += offset * 2 + CustomFontUtil.getStringWidth(gui.displayName)
        }

        return width
    }

    enum class Guis(
        val displayName : String,
        val gui0 : () -> GuiScreen,
        val check0 : () -> Boolean = { true },
        val open0 : () -> Unit = { },
        val close0 : () -> Unit = { },
        val init0 : (Int, Int) -> Unit = { _ : Int, _ : Int -> },
        val additions0 : () -> List<IElement> = { emptyList() }
    ) {
        ClickGui(
            displayName = "Click Gui",
            gui0 = { Kisman.instance.halqGui },
            open0 = { Kisman.instance.selectionBar.additions.add(Kisman.instance.halqGui.searchBar) },
            close0 = { Kisman.instance.selectionBar.additions.remove(Kisman.instance.halqGui.searchBar) },
            init0 = { x : Int, y : Int -> Kisman.instance.halqGui.searchBar.init(x, y) },
            additions0 = { listOf(Kisman.instance.halqGui.searchBar) }
        ),
        HudEditor("Hud Editor", { Kisman.instance.halqHudGui }),
        Music("Music", { Kisman.instance.musicGui }),
        Console("Console", { Kisman.instance.consoleGui })

        ;

        fun gui() : GuiScreen = if(check0()) gui0() else Kisman.instance.halqGui // TODO: gui that will says "this feature is not available for normal users"
        fun open() : Unit = mc.displayGuiScreen(gui())
    }
}