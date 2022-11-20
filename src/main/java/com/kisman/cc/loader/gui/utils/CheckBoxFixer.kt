package com.kisman.cc.loader.gui.utils

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * @author _kisman_
 * @since 12:31 of 20.11.2022
 */
class CheckBoxFixer : MouseListener {
    override fun mouseClicked(
        e : MouseEvent?
    ) {
        FlatMaterialDarkerIJTheme.updateUI()
    }

    override fun mousePressed(
        e : MouseEvent?
    ) {
        FlatMaterialDarkerIJTheme.updateUI()
    }

    override fun mouseReleased(
        e : MouseEvent?
    ) {
        FlatMaterialDarkerIJTheme.updateUI()
    }

    override fun mouseEntered(
        e : MouseEvent?
    ) {
        FlatMaterialDarkerIJTheme.updateUI()
    }

    override fun mouseExited(
        e : MouseEvent?
    ) {
        FlatMaterialDarkerIJTheme.updateUI()
    }
}