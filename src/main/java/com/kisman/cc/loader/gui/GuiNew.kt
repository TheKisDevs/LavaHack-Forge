/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.loader.gui

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme
import com.kisman.cc.loader.gui.page.Page
import com.kisman.cc.loader.gui.page.pages.ConsolePage
import com.kisman.cc.loader.gui.page.pages.MainPage
import com.kisman.cc.loader.gui.page.pages.SettingsPage
import com.kisman.cc.loader.gui.utils.LoaderFont
import com.kisman.cc.loader.version
import java.awt.*
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.UIManager

val pages = mutableMapOf<Class<*>, Page?>(
    MainPage::class.java to null,
    ConsolePage::class.java to null,
    SettingsPage::class.java to null
)

val fonts = mutableListOf<LoaderFont>()

var frame : JFrame? = null
var tabs : JTabbedPane? = null

var created = false

fun create() {
    fonts += LoaderFont(
        "Default",
        defaultFont
    )

    val fontCache = HashSet<String>()

    for(font in GraphicsEnvironment.getLocalGraphicsEnvironment().allFonts) {
        if(!fontCache.contains(font.fontName)) {
            fonts += LoaderFont(
                font.fontName,
                font.deriveFont(fontSize).also { GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(it) }
            )

            fontCache += font.fontName
        }
    }

//    FlatMaterialDarkerIJTheme.setup()

    frame = JFrame("LavaHack Loader | $version")
    frame!!.setSize(500, 400)

    val tabsPanel = JPanel()
    tabsPanel.layout = GridLayout(1, 1)

    tabs = JTabbedPane()

    pages
        .map {
            pages[it.key] = it.key.getDeclaredConstructor().newInstance() as Page
            pages[it.key]!!
        }
        .forEach { tabs!!.addTab(it.pageName, it.apply { init() }) }

    tabs!!.font

    tabsPanel.add(tabs)
    frame!!.add(tabsPanel, BorderLayout.CENTER)

    frame!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame!!.isResizable = false
    frame!!.isVisible = true

//    FlatMaterialDarkerIJTheme.updateUI()

    created = true
}

fun close() {
    frame?.isVisible = false
}

fun log(
    data : String
) {
    ConsolePage.instance!!.log(data)
}

fun updateTabs() {
    tabs!!.font = currentFont
}

val accentColor: Color
    get() {
        var accentColor : Color? = null

        for ((key) in UIManager.getDefaults().entries) {
            if (key.toString().contains("accent")) {
                accentColor = UIManager.getColor(key)
                if (key == "accent") break
            }
        }

        return accentColor ?: Color.ORANGE
    }

val defaultFont = JLabel().font!!
val fontSize = defaultFont.size2D

var currentFont = defaultFont