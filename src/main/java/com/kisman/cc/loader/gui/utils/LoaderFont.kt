package com.kisman.cc.loader.gui.utils

import java.awt.Font

/**
 * @author _kisman_
 * @since 21:23 of 02.09.2022
 */
class LoaderFont(
    private val name : String,
    val font : Font
) {
    override fun toString() : String {
        return name
    }
}