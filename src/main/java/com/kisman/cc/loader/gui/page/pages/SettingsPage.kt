/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui.page.pages

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme
import com.kisman.cc.loader.gui.components.ComponentsPair
import com.kisman.cc.loader.gui.currentFont
import com.kisman.cc.loader.gui.fonts
import com.kisman.cc.loader.gui.page.Page
import com.kisman.cc.loader.gui.updateTabs
import com.kisman.cc.loader.gui.utils.LoaderFont
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel

class SettingsPage : Page("Settings") {
    override fun init() {
        val themeLabel = JLabel("Theme: ")
        themeLabel.preferredSize = Dimension(50, 25)

        val themeComboBox = JComboBox(FlatAllIJThemes.INFOS.map { it.name }.toTypedArray())
        themeComboBox.preferredSize = Dimension(200, 25)
        themeComboBox.addActionListener {
            try {
                Class.forName(
                    FlatAllIJThemes.INFOS
                        .first {
                            it.name == (themeComboBox.selectedItem?.toString()
                                ?: return@addActionListener)
                        }
                        .className
                ).getMethod("setup").invoke(null)

                FlatLaf.updateUI()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }

        fontableElements += themeLabel
        fontableElements += themeComboBox


        val theme = ComponentsPair(themeLabel, themeComboBox, 250, 25)
        theme.setBounds(5, 5, 250, 25)
        add(theme)


        val overwritingCheckBox = JCheckBox("Overwriting")
        overwritingCheckBox.setBounds(5, 5 + 25, 250, 25)
        overwritingCheckBox.preferredSize = Dimension(200, 25)
        overwritingCheckBox.addMouseListener (object : MouseListener {
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
        })
        overwritingCheckBox.addActionListener {
            FlatMaterialDarkerIJTheme.updateUI()
        }

        fontableElements += overwritingCheckBox

        add(overwritingCheckBox)


        val pingBypassServerCheckBox = JCheckBox("Ping Bypass Server")
        pingBypassServerCheckBox.setBounds(5, 5 + 25 + 25, 250, 25)
        pingBypassServerCheckBox.preferredSize = Dimension(200, 25)
        pingBypassServerCheckBox.addMouseListener (object : MouseListener {
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
        })
        pingBypassServerCheckBox.addActionListener {
            FlatMaterialDarkerIJTheme.updateUI()
        }

        fontableElements += pingBypassServerCheckBox

        add(pingBypassServerCheckBox)


        val fontsLabel = JLabel("Font: ")
        fontsLabel.preferredSize = Dimension(50, 25)

        val fontComboBox = JComboBox(fonts.toTypedArray())
        fontComboBox.preferredSize = Dimension(200, 25)
        fontComboBox.addActionListener {
            currentFont = (fontComboBox.selectedItem as LoaderFont).font
            FlatLaf.updateUI()
            updateUI()
            updateTabs()
        }

        fontableElements += fontsLabel
        fontableElements += fontComboBox

        val fontTheme = ComponentsPair(fontsLabel, fontComboBox, 250, 25)
        fontTheme.setBounds(5, 5 + 25 + 25 + 25, 250, 25)
        add(fontTheme)
    }
}