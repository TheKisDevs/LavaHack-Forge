package com.kisman.cc.gui.api

/**
 * @author _kisman_
 * @since 14.05.2022
 */
interface Openable : Component {
    fun isOpen() : Boolean
    fun getComponents() : ArrayList<Component>
}