/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui.page

import com.kisman.cc.loader.gui.accentColor
import com.kisman.cc.loader.gui.components.RoundedBorder
import com.kisman.cc.loader.gui.currentFont
import java.awt.Component
import javax.swing.JPanel

@Suppress("SENSELESS_COMPARISON")
abstract class Page(
    val pageName : String
)  : JPanel() {
    protected val colorableBorders = ArrayList<JPanel>()
    protected val fontableElements = ArrayList<Component>()
    init {
        layout = null
    }

    abstract fun init()

    override fun updateUI() {
        if(colorableBorders == null || fontableElements == null) {
            return
        }

        for(element in colorableBorders) {
            (element.border as RoundedBorder).color = accentColor
        }

        for(element in fontableElements) {
            element.font = currentFont
        }
    }
}