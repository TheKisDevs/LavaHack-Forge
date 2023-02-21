package com.kisman.cc.features.hud.modules.arraylist

import com.kisman.cc.features.DisplayableFeature
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 21:56 of 17.05.2022
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class ArrayListElement(
    val element : DisplayableFeature,
    val name : String,
    val raw : String,
    val type : ElementTypes
) {
    @JvmField var done = false

    constructor(
        element : DisplayableFeature,
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