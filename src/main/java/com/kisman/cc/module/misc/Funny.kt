package com.kisman.cc.module.misc

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.NoRenderPig
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
