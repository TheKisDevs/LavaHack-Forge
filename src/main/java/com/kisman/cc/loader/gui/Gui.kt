package com.kisman.cc.loader.gui

import com.kisman.cc.loader.Utility
import com.kisman.cc.loader.load
import com.kisman.cc.loader.version
import com.kisman.cc.loader.versions
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.text.BadLocationException


/**
 * @author _kisman_
 * @since 14:04 of 02.08.2022
 */
@Suppress("NAME_SHADOWING", "UNUSED_VARIABLE")
class Gui(
    width : Int,
    height : Int,
    title : String,
    private val montserrat : Font
) : JFrame(title) {
    private val screenSize = Toolkit.getDefaultToolkit().screenSize
    private val logArea : JTextArea

    init {
        try {
            iconImage = ImageIO.read(javaClass.classLoader.getResourceAsStream("assets/loader/z.png"))
        } catch(e : IOException) {
            e.printStackTrace()
        }

        setBounds(screenSize.width / 2 - (width / 2), screenSize.height / 2 - (height / 2) , width, height);
        layout = null
        isResizable = false
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(windowEvent : WindowEvent) {
                Utility.unsafeCrash()
            }
        })

        val versionLabel = JLabel("Version").also {
            it.setBounds(8, 12, 100, 25)
            font(it)
            add(it)
        }

        val versionComboBox = JComboBox<String>(versions).also {
            it.setBounds(80, 10, 260, 30)
            font(it)
            add(it)
        }

        val keyLabel = JLabel("Key").also {
            it.setBounds(8, 52, 100, 25)
            font(it)
            add(it)
        }

        val keyField = JTextField().also {
            it.setBounds(80, 50, 260, 30)
            font(it)
            add(it)
        }

        val loginButton = JButton("Login").also {
            it.setBounds(5, 175, 340, 30)
            it.isFocusPainted = false
            it.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if(keyField.text.isNotEmpty()) {
                        Thread {
                            load(
                                keyField.text.toString(),
                                version,
                                Utility.properties(),
                                Runtime.getRuntime().availableProcessors().toString(),
                                Utility.stringFixer(versions[versionComboBox.selectedIndex])
                            )
                        } .start()
                    }
                }
            })
            font(it)
            add(it)
        }

        logArea = JTextArea().also {
            val chatPanel = JPanel(BorderLayout())
            chatPanel.setBounds(5, 90, 340, 78)
            add(chatPanel)

            it.wrapStyleWord = true
            it.background = Color(0x46494b, false)
            it.lineWrap = true
            it.isEditable = false
            font(it)
            chatPanel.add(it)

            val scrollPane = JScrollPane(it)
            chatPanel.add(scrollPane)
            scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
    }

    private fun font(component : Component) {
        component.font = montserrat
    }

    fun log(data : String) {
        logArea.append("${if(logArea.text.isNotEmpty()) "\n" else ""}$data")
        try {
            logArea.caretPosition = logArea.getLineStartOffset(logArea.lineCount - 1)
        } catch (e : BadLocationException) {
            e.printStackTrace()
        }
    }
}