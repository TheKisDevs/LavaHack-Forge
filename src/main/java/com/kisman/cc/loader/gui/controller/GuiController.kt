package com.kisman.cc.loader.gui.controller

import com.kisman.cc.loader.*
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*


/**
 * @author _kisman_
 * @since 2:22 of 01.08.2022
 */
@Suppress("PropertyName", "ControlFlowWithEmptyBody")
class GuiController : Initializable {
    @JvmField var selectedVersion : ComboBox<*>? = null
    @JvmField var tt_auth : Tooltip? = null
    @JvmField var tt_version : Tooltip? = null
    @JvmField var tt_key : Tooltip? = null
    @JvmField var key : TextField? = null
    @JvmField var auth : Button? = null
    @JvmField var exit : Button? = null
    @JvmField var mainBackground : AnchorPane? = null
    @JvmField var label : Label? = null

    fun exit(event : ActionEvent) {
        Utility.unsafeCrash()
    }

    fun login(event : ActionEvent) {
        if(key!!.text.isNotEmpty()) {
            load(
                key!!.text,
                version,
                Utility.properties(),
                Runtime.getRuntime().availableProcessors().toString()
            )
        }
    }

    override fun initialize(location : URL?, resources : ResourceBundle?) {
        setToolTips()
        labelThread()
        setBackground()
    }

    private fun setBackground() {
        mainBackground?.style = "-fx-background-image: url('/com/kisman/cc/loader/gui/css/images/background.png')"
    }

    private fun labelThread() {
        Thread {
            while(true) {
                label?.text = status
            }
        } .start()
    }

    private fun setToolTips() {
        val image = Image(javaClass.getResourceAsStream("/com/kisman/cc/loader/gui/css/images/tooltip.png"))

        tt_auth?.text = "Runs minecraft."
        tt_auth?.graphic = ImageView(image)

        tt_key?.text = "Put in your license key."
        tt_key?.graphic = ImageView(image)

        tt_version?.text = "Select current version."
        tt_version?.graphic = ImageView(image)
    }
}