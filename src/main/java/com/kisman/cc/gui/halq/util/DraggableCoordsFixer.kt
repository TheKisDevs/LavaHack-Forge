package com.kisman.cc.gui.halq.util

import com.kisman.cc.gui.api.Draggable
import com.kisman.cc.util.Globals
import net.minecraft.client.gui.ScaledResolution

/**
 * @author _kisman_
 * @since 22:09 of 20.06.2022
 */
class DraggableCoordsFixer {
    companion object {
        fun fix(draggable : Draggable) {
            val sr = ScaledResolution(Globals.mc)

            draggable.setX(draggable.getX().coerceIn(0.0, sr.scaledWidth - draggable.getW()))
            draggable.setY(draggable.getY().coerceIn(0.0, sr.scaledHeight - draggable.getH()))
        }
    }
}