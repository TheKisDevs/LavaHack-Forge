package com.kisman.cc.gui.mainmenu.gui

import com.kisman.cc.Kisman
import com.kisman.cc.features.plugins.managers.PluginManager
import com.kisman.cc.gui.mainmenu.gui.element.elements.TextElement
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 18:53 of 15.06.2022
 */
class MainMenuController {
    val elements = ArrayList<TextElement>()

    fun init() {
        var count = 1

        val watermark = TextElement(TextFormatting.WHITE.toString() + Kisman.getName() + " " + TextFormatting.GRAY + Kisman.getVersion(), 1.0, count.toDouble())
        elements.add(watermark)

        for(config in PluginManager.getInstance().configs) {
            count++
            val element = TextElement("${config.value.displayName} ${TextFormatting.GRAY}${config.value.version}", 1.0, (count - 1) * watermark.getHeight() + count)
            elements.add(element)
        }
    }

    fun draw() {
        for(element in elements) {
            element.draw()
        }
    }
}