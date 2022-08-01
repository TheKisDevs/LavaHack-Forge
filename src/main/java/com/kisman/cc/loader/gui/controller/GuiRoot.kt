package com.kisman.cc.loader.gui.controller

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * @author _kisman_
 * @since 13:02 of 01.08.2022
 */
class GuiRoot : Application() {
    private var xOffset = 0.0
    private var yOffset = 0.0

    companion object {
        @JvmStatic fun main() {
            launch()
        }
    }

    @Throws(Exception::class) override fun start(stage : Stage) {
        println("Gui was started")

        val fxml = FXMLLoader.load<Parent>(javaClass.getResource("com/kisman/cc/loader/gui/gui.fxml"))
        val scene = Scene(fxml)

        println("Gui loaded FXML and current scene")

        stage.title = "LavaHack Loader"

        stage.initStyle(StageStyle.UNDECORATED)
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.minWidth = 450.0
        stage.minHeight = 450.0
        stage.maxWidth = 450.0
        stage.maxHeight = 450.0
        stage.isResizable = false

        scene.onMousePressed = EventHandler { event: MouseEvent ->
            xOffset = stage.x - event.screenX
            yOffset = stage.y - event.screenY
        }

        scene.onMouseDragged = EventHandler { event: MouseEvent ->
            stage.x = event.screenX + xOffset
            stage.y = event.screenY + yOffset
        }

        println("Gui setupped current scene")

        stage.scene = scene
        stage.show()

        println("Gui showed window")
    }
}