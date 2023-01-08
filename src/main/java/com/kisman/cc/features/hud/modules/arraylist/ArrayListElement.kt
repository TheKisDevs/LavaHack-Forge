package com.kisman.cc.features.hud.modules.arraylist

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 21:56 of 17.05.2022
 */
class ArrayListElement(
    val element : IArrayListElement,
    val name : String,
    val raw : String,
    val type : ElementTypes
) {
    constructor(
        element : IArrayListElement,
        name : String,
        type : ElementTypes
    ) : this(
        element,
        name,
        name,
        type
    )

    fun active() : Boolean = if(type == ElementTypes.Module || type == ElementTypes.HudModule) {
        (element as Module).isToggled
    } else {
        (element as Setting).valBoolean
    }
}