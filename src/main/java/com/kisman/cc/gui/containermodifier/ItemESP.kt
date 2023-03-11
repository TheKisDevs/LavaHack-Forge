package com.kisman.cc.gui.containermodifier

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiTextField
import net.minecraft.item.ItemStack

class ItemESP {
    var guiTextField = GuiTextField(9999, Minecraft.getMinecraft().fontRenderer, 0, 0, 0, 0)

    var itemStacks = ArrayList<ItemStack>()
    val offset = 5
    val height = 22

    fun update(
        x : Int,
        y : Int,
        width : Int,
        height : Int
    ) {
        guiTextField.x = x
        guiTextField.y = y - offset - this.height
        guiTextField.width = width
        guiTextField.height = this.height
    }
}