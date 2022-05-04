package com.kisman.cc.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventUpdateLightmap
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketTimeUpdate
import java.awt.Color
import javax.vecmath.Vector3f

class Changer : Module("Changer", "FullBright + CustomFov + Ambience + CustomTime", Category.CLIENT) {
    private val gamma = Setting("Gamma", this, 100.0, 1.0, 100.0, true)
    private val fov = Setting("Fov", this, 120.0, 30.0, 150.0, true)
    private val ambience = Setting("Ambience", this, false)

    //Ambience settings
    private val ambColor: Setting = Setting("Ambience Color", this, "Ambience Color", Colour(-1)).setVisible { ambience.valBoolean }

    private val time = Setting("Time", this, false)

    //Time settings
    private val timeVal = Setting("Time Value", this, 24.0, 5.0, 25.0, true).setVisible { time.valBoolean }
    private val timeInfCircle = Setting("Time Infinity Circle", this, true).setVisible { time.valBoolean }
    private val timeSpeed = Setting("Time Speed", this, 100.0, 10.0, 1000.0, Slider.NumberType.TIME).setVisible { time.valBoolean }

    var circle = 0
    private var oldFov = 0F

    init {
        setmgr.rSetting(gamma)
        setmgr.rSetting(fov)
        setmgr.rSetting(ambience)
        setmgr.rSetting(ambColor)
        setmgr.rSetting(time)
        setmgr.rSetting(timeVal)
        setmgr.rSetting(timeInfCircle)
        setmgr.rSetting(timeSpeed)
    }

    override fun onEnable() {
        Kisman.EVENT_BUS.subscribe(receive)
        Kisman.EVENT_BUS.subscribe(updateLightmap)
        oldFov = mc.gameSettings.fovSetting
    }

    override fun onDisable() {
        Kisman.EVENT_BUS.unsubscribe(updateLightmap)
        Kisman.EVENT_BUS.unsubscribe(receive)
        mc.gameSettings.gammaSetting = 1F
        mc.gameSettings.fovSetting = oldFov
    }

    override fun update() {
        if(mc.player == null || mc.world == null) return

        mc.gameSettings.gammaSetting = gamma.valFloat
        mc.gameSettings.fovSetting = fov.valFloat

        if(time.valBoolean) {
            circle += timeSpeed.valInt
            mc.world.worldTime = if (timeInfCircle.valBoolean) circle.toLong() else timeVal.valLong * 1000L
            if (circle >= 24000) circle = 0
        }
    }

    val receive = Listener<PacketEvent.Receive>(EventHook {
        if(time.valBoolean && it.packet is SPacketTimeUpdate) {
            it.cancel()
        }
    })

    private val updateLightmap = Listener<EventUpdateLightmap.Post>(EventHook {
        if(ambience.valBoolean) {
            for (i in it.lightmapColors.indices) {
                val ambientColor: Color = ambColor.colour.color
                val alpha = ambientColor.alpha
                val modifier = alpha.toFloat() / 255.0f
                val color: Int = it.lightmapColors[i]
                val bgr: IntArray = toRGBAArray(color)
                val values = Vector3f(bgr[2].toFloat() / 255.0f, bgr[1].toFloat() / 255.0f, bgr[0].toFloat() / 255.0f)
                val newValues = Vector3f(ambientColor.red.toFloat() / 255.0f, ambientColor.green.toFloat() / 255.0f, ambientColor.blue.toFloat() / 255.0f)
                val finalValues: Vector3f = mix(values, newValues, modifier)
                val red = (finalValues.x * 255.0f).toInt()
                val green = (finalValues.y * 255.0f).toInt()
                val blue = (finalValues.z * 255.0f).toInt()
                it.lightmapColors[i] = -0x1000000 or (red shl 16) or (green shl 8) or blue
            }
            it.cancel()
        }
    })

    private fun toRGBAArray(colorBuffer: Int): IntArray {
        return intArrayOf(colorBuffer shr 16 and 0xFF, colorBuffer shr 8 and 0xFF, colorBuffer and 0xFF)
    }

    private fun mix(first: Vector3f, second: Vector3f, factor: Float): Vector3f {
        return Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor)
    }
}