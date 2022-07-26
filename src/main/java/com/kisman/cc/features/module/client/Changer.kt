package com.kisman.cc.features.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventArmSwingAnimationEnd
import com.kisman.cc.event.events.EventAspect
import com.kisman.cc.event.events.EventUpdateLightmap
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
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
import com.kisman.cc.util.math.max

class Changer : Module("Changer", "Changes your minecraft", Category.CLIENT) {
    private val gamma = register(Setting("Gamma", this, 100.0, 1.0, 100.0, true))
    private val fov = register(Setting("Fov", this, 120.0, 30.0, 150.0, true))

    //Ambience settings
    private val ambienceGroup = register(SettingGroup(Setting("Ambience", this)))
    private val ambience = register(ambienceGroup.add(Setting("Ambience", this, false)))
    private val ambienceColor = register(ambienceGroup.add(Setting("Ambience Color", this, "Color", Colour(-1)).setVisible(ambience)))

    //Time settings
    private val timeGroup = register(SettingGroup(Setting("Time", this)))
    private val time = register(timeGroup.add(Setting("Time", this, false)))
    private val timeVal = register(timeGroup.add(Setting("Time Value", this, 24.0, 5.0, 25.0, true).setVisible(time).setTitle("Value")))
    private val timeInfCircle = register(timeGroup.add(Setting("Time Infinity Circle", this, true).setVisible(time).setTitle("Infinity Circle")))
    private val timeSpeed = register(timeGroup.add(Setting("Time Speed", this, 100.0, 10.0, 1000.0, NumberType.TIME).setVisible(time).setTitle("Speed")))

    //Aspect settings
    private val aspectGroup = register(SettingGroup(Setting("Aspect", this)))
    private val aspect = register(aspectGroup.add(Setting("Aspect", this, false)))
    private val aspectWidth = register(aspectGroup.add(Setting("Aspect Width", this, 4.0, 1.0, 10.0, true).setVisible(aspect).setTitle("Width")))
    private val aspectHeight = register(aspectGroup.add(Setting("Aspect Height", this, 3.0, 1.0, 10.0, true).setVisible(aspect).setTitle("Height")))

    //CustomFog settings
    private val customFogGroup = register(SettingGroup(Setting("Custom Fog", this)))
    private val customFog = register(customFogGroup.add(Setting("Custom Fog", this, false)))
    private val customFogColor = register(customFogGroup.add(Setting("Custom Fog Color", this, "Custom Fog Color", Colour(-1)).setVisible(customFog).setTitle("Color")))

    //Swing setting
    private val swingGroup = register(SettingGroup(Setting("Swing", this)))
    private val swing = register(swingGroup.add(Setting("Swing", this, false)))
    private val swingHand = register(swingGroup.add(Setting("Swing Hand", this, SwingHands.MainHand).setVisible(swing).setTitle("Hand")))

    //Animation setting
    private val animationGroup = register(SettingGroup(Setting("Animation", this)))
    private val animation = register(animationGroup.add(Setting("Animation", this, false)))
    private val animationSpeed = register(animationGroup.add(Setting("Animation Speed", this, 13.0, 1.0, 20.0, true).setVisible(animation).setTitle("Speed")))

    //Shadow Text Modifier settings
    private val shadowTextModifierGroup = register(SettingGroup(Setting("Shadow Text Modifier", this)))
    val shadowTextModifier = register(shadowTextModifierGroup.add(Setting("Shadow Text Modifier", this, false).setTitle("Modify")))
    val shadowX = register(shadowTextModifierGroup.add(Setting("Shadow X", this, 1.0, 0.0, 2.0, false).setVisible(shadowTextModifier)))
    val shadowY = register(shadowTextModifierGroup.add(Setting("Shadow Y", this, 1.0, 0.0, 2.0, false).setVisible(shadowTextModifier)))

    //Timer settings
    private val timerGroup = register(SettingGroup(Setting("Timer", this)))
    private val timer = register(timerGroup.add(Setting("Timer", this, false)))
    private val timerSpeed = register(timerGroup.add(Setting("Timer Speed", this, 4.0, 0.1, 10.0, false).setVisible(timer).setTitle("Speed")))

    private var circle = 0
    private var oldFov = 0F

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
        Kisman.EVENT_BUS.subscribe(updateLightmap)
        Kisman.EVENT_BUS.subscribe(aspectEvent)
        Kisman.EVENT_BUS.subscribe(animationListener)
        oldFov = mc.gameSettings.fovSetting
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(animationListener)
        Kisman.EVENT_BUS.unsubscribe(aspectEvent)
        Kisman.EVENT_BUS.unsubscribe(updateLightmap)
        Kisman.EVENT_BUS.unsubscribe(receive)
        mc.timer.tickLength = 50f
        mc.gameSettings.gammaSetting = 1F
        mc.gameSettings.fovSetting = oldFov
        if(mc.player == null || mc.world == null) return
        mc.player.swingingHand = EnumHand.MAIN_HAND
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            mc.timer.tickLength = 50f
            return
        }

        mc.gameSettings.gammaSetting = gamma.valFloat
        mc.gameSettings.fovSetting = fov.valFloat

        if(time.valBoolean) {
            circle += timeSpeed.valInt
            mc.world.worldTime = if (timeInfCircle.valBoolean) circle.toLong() else timeVal.valLong * 1000L
            if (circle >= 24000) circle = 0
        }

        doSwing()
        doTimer()
    }

    private fun doTimer() {
        if(timer.valBoolean) {
            mc.timer.tickLength = 50f / timerSpeed.valFloat.max(0.1f)
        } else {
            mc.timer.tickLength = 50f
        }
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

    private val animationListener = Listener<EventArmSwingAnimationEnd>(EventHook {
        if(animation.valBoolean) {
            it.armSwingAnimationEnd = animationSpeed.valInt
        }
    })

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
                val ambientColor: Color = ambienceColor.colour.color
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