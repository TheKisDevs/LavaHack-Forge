package com.kisman.cc.gui.halq.components.sub.hud

import com.kisman.cc.gui.api.Component
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.gui.halq.util.HudModuleCoordsFixer
import com.kisman.cc.util.render.Render2DUtil
import java.awt.Color

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class Draggable(
        val module : HudModule
) : Component {
    var dragX = 0
    var dragY = 0
    var drag = false

    override fun drawScreen(mouseX: Int, mouseY: Int) {
        if(module.isToggled) {
            if(drag) {
                module.x = (mouseX - dragX).toDouble()
                module.y = (mouseY - dragY).toDouble()
            }

            HudModuleCoordsFixer.fix(module)

            Render2DUtil.drawRectWH(module.x, module.y, module.w, module.h, Color(10, 10, 10, 170).rgb)
        } else {
            drag = false
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if(module.isToggled) {
            drag = isMouseOnButton(mouseX, mouseY)
            dragX = (mouseX - module.x).toInt()
            dragY = (mouseY - module.y).toInt()
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        drag = false
    }

    private fun isMouseOnButton(x : Int, y : Int) : Boolean {
        return x > module.x && x < module.x + module.w && y > module.y && y < module.y + module.h
    }
}