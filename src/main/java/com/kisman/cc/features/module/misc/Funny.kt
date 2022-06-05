package com.kisman.cc.features.module.misc

import com.kisman.cc.Kisman
import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Globals
import com.kisman.cc.util.entity.NoRenderPig
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.chat.cubic.ChatUtility
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
    private val kismansDupe = register(Setting("Kismans Dupe", this, false))

    private val ssTimer = TimerUtils()
    private var lastSS = false
    private var lastKD = false

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

    private fun doKismansDupe() {
        if(kismansDupe.valBoolean) {
            if(!lastKD) {
                kismansDupe.valBoolean = false
            }

            if(mc.player.heldItemMainhand.isEmpty) {
                ChatUtility.error().printClientModuleMessage("You need to hold an item in hand to dupe!!!!!")
            } else {
                val count = Globals.random.nextInt(31) + 1

                for(i in 0..count) {
                    val itemE = mc.player.dropItem(mc.player.heldItemMainhand.copy(), false, true)
                    if(itemE != null) mc.world.addEntityToWorld(itemE.entityId, itemE)
                }

                val total = count * mc.player.heldItemMainhand.count
                mc.player.sendChatMessage("I just uses TheLavaDupe and got $total ${mc.player.heldItemMainhand.displayName}, thanks to ${Kisman.getName()}")
            }
            kismansDupe.valBoolean = false;

        }

        lastKD = kismansDupe.valBoolean
    }
    
    private fun doSneakSpam() {
        if(!lastSS && sneakSpam.valBoolean) {
            ssTimer.reset()
        }
        if(sneakSpam.valBoolean) {
            if(ssTimer.passedMillis(ssDelay.valLong)) {
                ssTimer.reset()
                mc.gameSettings.keyBindSneak.pressed = !mc.gameSettings.keyBindSneak.pressed;
            }
        }
        lastSS = sneakSpam.valBoolean
    }

    private fun doPigPOV() {
        if(pigPov.valBoolean) {
            mc.player.eyeHeight = 0.6f
            mc.renderManager.entityRenderMap[EntityPig::class.java] =
                NoRenderPig(mc.renderManager, mc)
        } else {
            mc.player.eyeHeight = mc.player.defaultEyeHeight
            mc.renderManager.entityRenderMap[EntityPig::class.java] = RenderPig(mc.renderManager)
        }
    }
}
