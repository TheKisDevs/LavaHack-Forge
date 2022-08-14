package com.kisman.cc.util.minecraft

import com.google.gson.JsonSyntaxException
import com.kisman.cc.Kisman
import com.kisman.cc.util.Globals.mc
import net.minecraft.client.renderer.EntityRenderer
import java.io.IOException

/**
 * @author _kisman_
 * @since 13:53 of 24.07.2022
 */
object EntityRendererUtil {
    fun load(name : String, shader : String) {
        try {
            mc.entityRenderer.shaderGroup = ShaderGroupHandler(
                mc.textureManager,
                mc.entityRenderer.resourceManager,
                mc.framebuffer,
                ResourceLocationHandler(name, shader)
            )
            mc.entityRenderer.shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
            mc.entityRenderer.useShader = true
        } catch (var3: IOException) {
            Kisman.LOGGER.warn("Failed to load shader: $name", var3)
            mc.entityRenderer.shaderIndex = EntityRenderer.SHADER_COUNT
            mc.entityRenderer.useShader = false
        } catch (var4: JsonSyntaxException) {
            EntityRenderer.LOGGER.warn("Failed to load shader: $name", var4)
            mc.entityRenderer.shaderIndex = EntityRenderer.SHADER_COUNT
            mc.entityRenderer.useShader = false
        }
    }
}