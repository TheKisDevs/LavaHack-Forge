package com.kisman.cc.util.enums

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.gui.api.Draggable
import com.kisman.cc.util.sr

/**
 * @author _kisman_
 * @since 11:29 of 29.03.2023
 */
@Suppress("unused")
enum class LinkedPlaces(
    val modifier : IModifier,
    val right : Boolean?,
    val reverse : Boolean?
) {
    None(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) { }
    }, null, null),
    LeftTop(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) {
            draggable.setX(Kisman.instance.hudModuleManager.offsetX.toDouble())
            draggable.setY(height)
        }
    }, false, false),
    LeftBottom(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) {
            val sr = sr()

            draggable.setX(Kisman.instance.hudModuleManager.offsetX.toDouble())
            draggable.setY(sr.scaledHeight - height - draggable.getH())
        }
    }, false, true),
    RightTop(object : IModifier {
        override fun modify(
            draggable: Draggable,
            height : Double
        ) {
            val sr = sr()

            draggable.setX(sr.scaledWidth - draggable.getW() - Kisman.instance.hudModuleManager.offsetX)
            draggable.setY(height)
        }
    }, true, false),
    RightBottom(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) {
            val sr = sr()

            draggable.setX(sr.scaledWidth - draggable.getW() - Kisman.instance.hudModuleManager.offsetX)
            draggable.setY(sr.scaledHeight - height - draggable.getH())
        }
    }, true, true),
    MiddleTop(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) {
            val sr = sr()

            draggable.setX(sr.scaledWidth / 2.0 - draggable.getW() / 2.0)
            draggable.setY(height)
        }
    }, true, false),
    MiddleBottom(object : IModifier {
        override fun modify(
            draggable : Draggable,
            height : Double
        ) {
            val sr = sr()

            draggable.setX(sr.scaledWidth / 2.0 - draggable.getW() / 2.0)
            draggable.setY(sr.scaledHeight - height - draggable.getH())
        }
    }, true, false)

    ;

    val modules = mutableSetOf<HudModule>()
    protected val height : Double
        get() {
            val offset = Kisman.instance.hudModuleManager?.offsetY ?: 1
            var height = 0.0

            for(module in modules) {
                if(module.isToggled) {
                    height += module.getH() + offset
                }
            }

            return height
        }

    fun add(
        module : HudModule
    ) {
        module.place = this
        modifier.modify(module, height)
        modules.add(module)
    }

    fun move(
        module : HudModule
    ) {
        remove(module)
        add(module)
        refresh()
    }

    fun remove(
        module : HudModule
    ) {
        module.place.modules.remove(module)
        module.place = None
    }

    fun refresh() {
        val offset = Kisman.instance.hudModuleManager?.offsetY ?: 1
        var height = 0.0

        for(module in modules) {
            if(module.isToggled) {
                modifier.modify(module, height)

                height += module.getH() + offset
            }
        }
    }

    interface IModifier {
        fun modify(
            draggable : Draggable,
            height : Double
        )
    }
}