/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui.page.pages

import com.kisman.cc.loader.antidump.AntiDump
import com.kisman.cc.loader.gui.accentColor
import com.kisman.cc.loader.gui.components.ComponentsPair
import com.kisman.cc.loader.gui.components.RoundedBorder
import com.kisman.cc.loader.gui.getVersions
import com.kisman.cc.loader.gui.onLogin
import com.kisman.cc.loader.gui.page.Page
import com.kisman.cc.loader.versions
import java.awt.Dimension
import javax.swing.*

class MainPage : Page("Main") {
    override fun init() {
        val authenticationBox = JPanel()
        authenticationBox.setBounds(125, 75, 250, 150)
        authenticationBox.preferredSize = Dimension(250, 150)
        authenticationBox.border = RoundedBorder(20, accentColor, 2f)

        val keyLabel = JLabel("Key: ")
        keyLabel.preferredSize = Dimension(50, 25)

        val keyTextField = JTextField("Key")
        keyTextField.preferredSize = Dimension(100, 25)

        val versionLabel = JLabel("Version: ")
        versionLabel.preferredSize = Dimension(55, 25)

        val versionComboBox = JComboBox(getVersions())
        versionComboBox.preferredSize = Dimension(100, 25)

        val installButton = JButton("Install")
        installButton.preferredSize = Dimension(100, 25)
        installButton.addActionListener {
            if(AntiDump.check(keyTextField.text)) {
                onLogin(
                    keyTextField.text,
                    versions[versionComboBox.selectedIndex]
                )
            }
        }

        authenticationBox.add(ComponentsPair(keyLabel, keyTextField, 150, 25))
        authenticationBox.add(ComponentsPair(versionLabel, versionComboBox, 150, 25))
        authenticationBox.add(installButton)

        add(authenticationBox)

        colorableBorders += authenticationBox

        fontableElements += keyLabel
        fontableElements += keyTextField
        fontableElements += versionLabel
        fontableElements += versionComboBox
        fontableElements += installButton
    }
}