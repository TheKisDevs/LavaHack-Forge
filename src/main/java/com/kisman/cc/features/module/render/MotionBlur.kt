@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.minecraft.EntityRendererUtil
import java.lang.reflect.Field

/**
 * @author _kisman_
 * @since 15:56 of 23.07.2022
 */
const val MOTION_BLUR_SHADER =
        "{" +
            "\\\"targets\\\":[" +
                "\\\"swap\\\"," +
                "\\\"previous\\\"" +
            "]," +
            "\\\"passes\\\":[" +
                "{" +
                    "\\\"name\\\":\\\"phosphor\\\"," +
                    "\\\"intarget\\\":\\\"minecraft:main\\\"," +
                    "\\\"outtarget\\\":\\\"swap\\\"," +
                    "\\\"auxtargets\\\":[" +
                        "{" +
                            "\\\"name\\\":\\\"PrevSampler\\\"," +
                            "\\\"id\\\":\\\"previous\\\"" +
                        "}" +
                    "]," +
                    "\\\"uniforms\\\":[" +
                        "{" +
                            "\\\"name\\\":\\\"Phosphor\\\"," +
                            "\\\"values\\\":[" +
                                "%.2f, %.2f, %.2f" +
                            "]" +
                        "}" +
                    "]" +
                "}," +
                "{" +
                    "\\\"name\\\":\\\"blit\\\"," +
                    "\\\"intarget\\\":\\\"swap\\\"," +
                    "\\\"outtarget\\\":\\\"previous\\\"" +
                "},{" +
                    "\\\"name\\\":\\\"blit\\\"," +
                    "\\\"intarget\\\":\\\"swap\\\"," +
                    "\\\"outtarget\\\":\\\"minecraft:main\\\"" +
                "}" +
            "]" +
        "}"

@WorkInProgress
class  MotionBlur : Module(
    "MotionBlur",
    Category.RENDER
) {
    private val amount = register(Setting("Amount", this, 1.0, 1.0, 7.0, true))
    private var lastAmount = 0

    private val cachedFastRender : Field? = try {
        mc.gameSettings::class.java.getDeclaredField("ofFastRender")
    } catch(ignored : Exception) {
        null
    }

    override fun onEnable() {
        if(mc.player == null || mc.world == null || fastRenderCheck()) {
            super.setToggled(false)
            return
        }

        if(mc.entityRenderer.shaderGroup != null) {
            mc.entityRenderer.shaderGroup.deleteShaderGroup()
        }

        if(amount.valInt > 0) {
            EntityRendererUtil.load("motionblur", MOTION_BLUR_SHADER.format(amount.valFloat, amount.valFloat, amount.valFloat))
            ChatUtility.complete().printClientModuleMessage("Enabled motion blur with ${amount.valInt} amount!")
            mc.entityRenderer.getShaderGroup().createBindFramebuffers(mc.displayWidth, mc.displayHeight)
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null || fastRenderCheck()) {
            super.setToggled(false)
            return
        }

        if(amount.valInt != lastAmount) {
            onEnable()
        }

        lastAmount = amount.valInt
    }

    private fun fastRenderCheck() : Boolean {
        if(isFastRenderEnabled()) {
            if (mc.player != null || mc.world != null) {
                ChatUtility.error().printClientModuleMessage("Motion Blur is not compatible with OptiFine's Fast Render.")
            }
            return true
        }
        return false
    }

    private fun isFastRenderEnabled() : Boolean {
        if(cachedFastRender != null) {
            try {
                return cachedFastRender[mc.gameSettings] as Boolean
            } catch (ignored : Exception) {}
        }
        return false
    }
}