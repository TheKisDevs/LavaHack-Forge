package com.kisman.cc.features.hud.modules.arraylist

/**
 * @author _kisman_
 * @since 21:56 of 17.05.2022
 */
class ArrayListElement(
    val name : String,
    val raw : String,
    val type : ElementTypes
) {
    constructor(name : String, type : ElementTypes) : this(name, name, type)
}