package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventAspect
import com.kisman.cc.event.events.EventUpdateLightmap
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.SwingHands
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.item.ItemSword
import net.minecraft.network.play.server.SPacketTimeUpdate
import net.minecraft.util.EnumHand
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import javax.vecmath.Vector3f

class Changer : Module("Changer", "FullBright + CustomFov + Ambience + CustomTime + Aspect + CustomFog", Category.CLIENT) {
    private val gamma = register(Setting("Gamma", this, 100.0, 1.0, 100.0, true))
    private val fov = register(Setting("Fov", this, 120.0, 30.0, 150.0, true))

    //Ambience settings
    private val ambience = register(Setting("Ambience", this, false))
    private val ambColor: Setting = register(Setting("Ambience Color", this, "Ambience Color", Colour(-1)).setVisible { ambience.valBoolean })

    //Time settings
    private val time = register(Setting("Time", this, false))
    private val timeVal = register(Setting("Time Value", this, 24.0, 5.0, 25.0, true).setVisible { time.valBoolean })
    private val timeInfCircle = register(Setting("Time Infinity Circle", this, true).setVisible { time.valBoolean })
    private val timeSpeed = register(Setting("Time Speed", this, 100.0, 10.0, 1000.0, Slider.NumberType.TIME).setVisible { time.valBoolean })

    //Aspect settings
    private val aspect = register(Setting("Aspect", this, false))
    private val aspectWidth = register(Setting("Aspect Width", this, 4.0, 1.0, 10.0, true))
    private val aspectHeight = register(Setting("Aspect Height", this, 3.0, 1.0, 10.0, true))

    //CustomFog settings
    private val customFog = register(Setting("Custom Fog", this, false))
    private val customFogColor = register(Setting("Custom Fog Color", this, "Custom Fog Color", Colour(-1)).setVisible { customFog.valBoolean })

    //Swing setting
    private val swing = register(Setting("Swing", this, false))
    private val swingHand = register(Setting("Swing Hand", this, SwingHands.MainHand).setVisible { swing.valBoolean })

    //Animation setting
    private val animation = register(Setting("Animation", this, false))
    private val animationSpeed = register(Setting("Animation Speed", this, 13.0, 1.0, 20.0, true).setVisible { animation.valBoolean })

    private var circle = 0
    private var oldFov = 0F

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
        Kisman.EVENT_BUS.subscribe(updateLightmap)
        Kisman.EVENT_BUS.subscribe(aspectEvent)
        oldFov = mc.gameSettings.fovSetting
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(aspectEvent)
        Kisman.EVENT_BUS.unsubscribe(updateLightmap)
        Kisman.EVENT_BUS.unsubscribe(receive)
        mc.gameSettings.gammaSetting = 1F
        mc.gameSettings.fovSetting = oldFov
        if(mc.player == null || mc.world == null) return
        mc.player.swingingHand = EnumHand.MAIN_HAND
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

        doSwing()
    }

    private fun doSwing() {
        if(swing.valBoolean) {
            when (swingHand.valEnum as SwingHands) {
                SwingHands.MainHand -> mc.player.swingingHand = EnumHand.MAIN_HAND
                SwingHands.OffHand -> mc.player.swingingHand = EnumHand.OFF_HAND
                SwingHands.PacketSwing -> {
                    if(mc.player.heldItemMainhand.item is ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                        mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1f
                        mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.heldItemMainhand
                    }
                }
            }
        }
    }

    @SubscribeEvent fun onFog(event : EntityViewRenderEvent.FogColors) {
        if(customFog.valBoolean) {
            event.red = customFogColor.colour.r1
            event.green = customFogColor.colour.g1
            event.blue = customFogColor.colour.b1
        }
    }

    private val aspectEvent = Listener<EventAspect>(EventHook {
        if(aspect.valBoolean) {
            it.aspect = aspectWidth.valFloat / aspectHeight.valFloat
        }
    })

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