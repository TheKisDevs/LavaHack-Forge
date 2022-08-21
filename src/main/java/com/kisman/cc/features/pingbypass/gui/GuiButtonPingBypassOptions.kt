package com.kisman.cc.features.pingbypass.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

/**
 * @author _kisman_
 * @since 1:02 of 20.08.2022
 */
class GuiButtonPingBypassOptions(
    id : Int,
    x : Int,
    y : Int
) : GuiButton(
    id,
    x,
    y,
    20,
    20,
    ""
) {
    override fun drawButton(
        mc : Minecraft,
        mouseX : Int,
        mouseY : Int,
        partialTicks : Float
    ) {
        if(visible) {
            super.drawButton(
                mc,
                mouseX,
                mouseY,
                partialTicks
            )

            GlStateManager.color(1f, 1f, 1f, 1f)

            mc.renderItem.renderItemAndEffectIntoGUI(
                ItemStack(Items.WRITABLE_BOOK),
                x + 2,
                y + 2
            )
        }
    }
}