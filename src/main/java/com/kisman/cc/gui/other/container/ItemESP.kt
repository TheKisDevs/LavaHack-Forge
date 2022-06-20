package com.kisman.cc.gui.other.container

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiTextField
import net.minecraft.item.ItemStack

class ItemESP {
    companion object {
        var guiTextField = GuiTextField(9999, Minecraft.getMinecraft().fontRenderer, 0, 0, 0, 0)
    }
    var itemStacks = ArrayList<ItemStack>()
    val offset = 5
    val height = 22

    fun init(x: Int, y: Int, width: Int, height: Int) {
        guiTextField = GuiTextField(9999, Minecraft.getMinecraft().fontRenderer, x, y - offset - this.height, width, this.height)
    }

    fun getGuiTextField(): GuiTextField {
        return guiTextField
    }
}