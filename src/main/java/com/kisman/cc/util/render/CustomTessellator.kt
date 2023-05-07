package com.kisman.cc.util.render

import net.minecraft.client.renderer.Tessellator

/**
 * @author _kisman_
 * @since 20:25 of 06.05.2023
 */
class CustomTessellator(
    size : Int
) : Tessellator(
    size
) {
    @JvmField var draw = false

    override fun draw() {
        if(draw) {
            super.draw()
        }
    }
}