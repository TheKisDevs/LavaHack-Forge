package com.kisman.cc.module.misc

import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.NoRenderPig
import com.kisman.cc.util.TimerUtils
import net.minecraft.client.renderer.entity.RenderPig
import net.minecraft.entity.passive.EntityPig

/**
 * @author _kisman_
 * @since 15:15 of 18.05.2022
 */
class Funny : Module(
    "Funny",
    "Just get fun.",
    Category.MISC
) {
    private val pigPov = register(Setting("Pig POV", this, false))
    private val sneakSpam = register(Setting("Sneak Spam", this, false))
    private val ssDelay = register(Setting("SS Delay", this, 100.0, 0.0, 1000.0, Slider.NumberType.TIME).setVisible { sneakSpam.valBoolean })

    private val ssTimer = TimerUtils()
    private var lastSS = false

    override fun onDisable() {
        if(mc.player == null || mc.world == null) return
        if(pigPov.valBoolean) {
            mc.player.eyeHeight = mc.player.defaultEyeHeight
            mc.renderManager.entityRenderMap[EntityPig::class.java] = RenderPig(mc.renderManager)
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null) return

        doPigPOV()
        doSneakSpam()
    }
    
    private fun doSneakSpam() {
        if(!lastSS && sneakSpam.valBoolean) {
            ssTimer.reset()
        }
        if(sneakSpam.valBoolean) {
            if(ssTimer.passedMillis(ssDelay.valLong)) {
                ssTimer.reset()
                mc.player.isSneaking = !mc.player.isSneaking
            }
        }
        lastSS = sneakSpam.valBoolean
    }

    private fun doPigPOV() {
        if(pigPov.valBoolean) {
            mc.player.eyeHeight = 0.6f
            mc.renderManager.entityRenderMap[EntityPig::class.java] = NoRenderPig(mc.renderManager, mc)
        } else {
            mc.player.eyeHeight = mc.player.defaultEyeHeight
            mc.renderManager.entityRenderMap[EntityPig::class.java] = RenderPig(mc.renderManager)
        }
    }
}
