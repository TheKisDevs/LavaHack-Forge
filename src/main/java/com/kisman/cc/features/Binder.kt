package com.kisman.cc.features

import com.kisman.cc.util.enums.BindType
import com.kisman.cc.util.client.interfaces.IBindable

/**
 * @author _kisman_
 * @since 10:58 of 25.08.2022
 */
class Binder(
    val name : String,
    private var type : BindType,
    var key : Int,
    var mouse : Int,
    private var hold : Boolean
) : IBindable {
    override fun getKeyboardKey() : Int = key

    override fun setKeyboardKey(
        key : Int
    ) {
        this.key = key
    }

    override fun getMouseButton() : Int = mouse

    override fun setMouseButton(
        button : Int
    ) {
        this.mouse = button
    }

    override fun getType() : BindType = type

    override fun setType(
        type : BindType
    ) {
        this.type = type;
    }

    override fun isHold() : Boolean = hold

    override fun setHold(
        hold : Boolean
    ) {
        this.hold = hold;
    }

    override fun getButtonName() : String = name
}