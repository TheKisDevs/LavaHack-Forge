package com.kisman.cc.util.render.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard

/**
 * @author _kisman_
 * @since 20:44 of 19.05.2022
 */
class TextFieldHandler(
    val font : FontRenderer,
    val x : Int,
    val y : Int,
    val w : Int,
    val h : Int,
    val focused_ : Boolean
) : GuiTextField(INDEX, font, x, y, w, h) {
    companion object {
        var INDEX = 1000
            get() {
                field++
                return field
            }
    }

    constructor(
        x : Int,
        y : Int,
        w : Int,
        h : Int
    ) : this(
        Minecraft.getMinecraft().fontRenderer,
        x,
        y,
        w,
        h,
        false
    )

    fun init() {
        Keyboard.enableRepeatEvents(true)
        maxStringLength = 256
        enableBackgroundDrawing = false
        isFocused = focused_
        text = ""
        setCanLoseFocus(false)
    }

    fun clone() {
        Keyboard.enableRepeatEvents(false)
    }
}