/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui.components

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel

class ComponentsPair<K : JComponent, V : JComponent>(
    private val first : K,
    private val second : V,
    width : Int? = null,
    height : Int? = null
) : JPanel() {
    init {
        layout = BorderLayout()
        preferredSize = Dimension(
            width ?: preferredSize.width,
            height ?: preferredSize.height
        )

        add(first, BorderLayout.WEST)
        add(second, BorderLayout.EAST)
    }
}