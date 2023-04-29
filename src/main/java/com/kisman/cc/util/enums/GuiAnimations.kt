package com.kisman.cc.util.enums

import com.kisman.cc.util.client.interfaces.IPositionableGui
import com.kisman.cc.util.render.cubicgl.CubicGL
import com.kisman.cc.util.sr
import org.lwjgl.opengl.GL11
import kotlin.math.min

/**
 * @author _kisman_
 * @since 11:33 of 23.04.2023
 */
enum class BasicGuiAnimations(
    val handler : IAnimation
) {
    DropDown(object : IAnimation {
        override fun open(
            gui : IPositionableGui,
            progress : Double
        ) {
            GL11.glTranslated(0.0, -gui.h() * (1.0 - progress), 0.0)
        }

        override fun close(
            gui : IPositionableGui,
            progress : Double
        ) {
            GL11.glTranslated(0.0, (sr().scaledHeight + min(gui.y(), 0.0)) * progress, 0.0)
        }
    }),

    CentralExpansion(object : IAnimation {
        override fun open(
            gui : IPositionableGui,
            progress : Double
        ) {
            CubicGL.scale(gui.x(), gui.y(), gui.w(), gui.h(), progress)
        }

        override fun close(
            gui : IPositionableGui,
            progress : Double
        ) {
            CubicGL.scale(gui.x(), gui.y(), gui.w(), gui.h(), 1.0 - progress)
        }
    })
}

enum class GuiAnimations(
    private val animation : BasicGuiAnimations?
) {
    None(null),
    DropDown(BasicGuiAnimations.DropDown),
    CentralExpansion(BasicGuiAnimations.CentralExpansion),
    Random(null) {
        override fun animation() = BasicGuiAnimations.values()[(0 until BasicGuiAnimations.values().size).random()]
    }

    ;

    open fun animation() = animation
}

interface IAnimation {
    fun open(
        gui : IPositionableGui,
        progress : Double
    )

    fun close(
        gui : IPositionableGui,
        progress : Double
    )
}