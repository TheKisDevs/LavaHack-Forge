package com.kisman.cc.loader.gui.page.pages

import com.kisman.cc.loader.gui.accentColor
import com.kisman.cc.loader.gui.components.RoundedBorder
import com.kisman.cc.loader.gui.page.Page
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.text.BadLocationException
import kotlin.properties.Delegates

/**
 * @author _kisman_
 * @since 23:07 of 01.09.2022
 */
class ConsolePage : Page("Console") {
    private var chatPanel by Delegates.notNull<JPanel>()
    private var logArea : JTextArea? = null

    companion object {
        var instance : ConsolePage? = null
    }

    override fun init() {
        instance = this

        logArea = JTextArea().also {
            chatPanel = JPanel(BorderLayout())
            chatPanel.setBounds(5, 5, 500 - 27, 400 - 83)
            chatPanel.preferredSize = Dimension(500 - 27, 400 - 83)
            chatPanel.border = RoundedBorder(20, accentColor, 2f)
            add(chatPanel)

            colorableBorders += chatPanel

            it.wrapStyleWord = true
            it.isEditable = false
            it.isFocusable = false
            chatPanel.add(it)

            fontableElements += it

            val scrollPane = JScrollPane(it)
            chatPanel.add(scrollPane)
            scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
    }

    fun log(
        data : String
    ) {
        logArea!!.append("${if(logArea!!.text.isNotEmpty()) "\n" else ""}$data")
        try {
            logArea!!.caretPosition = logArea!!.getLineStartOffset(logArea!!.lineCount - 1)
        } catch (_ : BadLocationException) { }
    }
}