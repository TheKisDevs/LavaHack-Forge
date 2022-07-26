@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.motionblur.MotionBlurResourceManager
import com.kisman.cc.mixin.mixins.accessor.AccessorSimpleReloadableResourceManager
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import net.minecraft.client.resources.FallbackResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.util.ResourceLocation
import org.cubic.Environment
import java.lang.reflect.Field

/**
 * @author _kisman_
 * @since 15:56 of 23.07.2022
 */
const val MOTION_BLUR_SHADER = "" +
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

class MotionBlur : Module(
    "MotionBlur",
    Category.RENDER
) {
    val amount = register(Setting("Amount", this, 1.0, 1.0, 7.0, true))

    private val cachedFastRender : Field? = try {
        mc.gameSettings::class.java.getDeclaredField("ofFastRender")
    } catch(ignored : Exception) {
        null
    }

    init {
        registerDomain()
    }

    override fun onEnable() {
        if(mc.player == null || mc.world == null ||fastRenderCheck()) {
            return
        }

        if(mc.entityRenderer.shaderGroup != null) {
            mc.entityRenderer.shaderGroup.deleteShaderGroup()
        }

        if(amount.valInt > 0) {
            mc.entityRenderer.loadShader(ResourceLocation("motionblur", "motionblur"))
            mc.entityRenderer.getShaderGroup().createBindFramebuffers(mc.displayWidth, mc.displayHeight)
        }
    }

    private fun registerDomain() {
        if(!(mc.resourceManager as AccessorSimpleReloadableResourceManager).domainResourceManagers().containsKey("motionblur")) {
            (mc.resourceManager as AccessorSimpleReloadableResourceManager).domainResourceManagers()["motionblur"] = MotionBlurResourceManager(mc.metadataSerializer_)
        }
    }

    private fun fastRenderCheck() : Boolean {
        if(isFastRenderEnabled()) {
            if (mc.player != null || mc.world != null) {
                ChatUtility.error().printClientModuleMessage("Motion Blur is not compatible with OptiFine's Fast Render.")
            }
            super.setToggled(false)
            return true
        }
        return false
    }

    private fun isFastRenderEnabled() : Boolean {
        if(cachedFastRender != null) {
            try {
                return cachedFastRender[mc.gameSettings] as Boolean
            } catch (ignored: Exception) {}
        }
        return false
    }
}