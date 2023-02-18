package com.kisman.cc.gui.hudeditor

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.halq.util.DraggableCoordsFixer
import com.kisman.cc.util.render.Render2DUtil
import java.awt.Color

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class DraggableBox(
        val module : HudModule
) : Component {
    var dragX = 0
    var dragY = 0
    var drag = false

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        if(module.isToggled) {
            if(drag) {
                module.setX((mouseX - dragX).toDouble())
                module.setY((mouseY - dragY).toDouble())
            }

            DraggableCoordsFixer.fix(module)

            Render2DUtil.drawRectWH(module.getX(), module.getY(), module.getW(), module.getH(), Kisman.instance.halqHudGui.color.rgb)
        } else {
            drag = false
        }
    }

    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        button : Int
    ) {
        if(module.isToggled) {
            drag = isMouseOnButton(mouseX, mouseY)
            dragX = (mouseX - module.getX()).toInt()
            dragY = (mouseY - module.getY()).toInt()
        }
    }

    override fun mouseReleased(
        mouseX : Int,
        mouseY : Int,
        mouseButton : Int
    ) {
        drag = false
    }

    override var width = 0
    override var count = 0

    private fun isMouseOnButton(
        x : Int,
        y : Int
    ) : Boolean = x > module.getX() && x < module.getX() + module.getW() && y > module.getY() && y < module.getY() + module.getH()
}