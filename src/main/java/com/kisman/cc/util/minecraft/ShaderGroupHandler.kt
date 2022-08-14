package com.kisman.cc.util.minecraft

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.kisman.cc.mixin.mixins.accessor.AccessorShaderGroup
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.Shader
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.client.util.JsonException
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author _kisman_
 * @since 13:53 of 24.07.2022
 */
@Suppress("CAST_NEVER_SUCCEEDS", "SENSELESS_COMPARISON", "LocalVariableName")
class ShaderGroupHandler(
    textureManager: TextureManager,
    private val resourceManager: IResourceManager,
    private val mainFramebuffer: Framebuffer,
    resourceLocation: ResourceLocation
) : ShaderGroup(
    textureManager,
    resourceManager,
    mainFramebuffer,
    resourceLocation
) {
    override fun parseGroup(textureManager: TextureManager, resourceLocation: ResourceLocation) {
        val jsonparser = JsonParser()
        var iresource: IResource? = null

        try {
            iresource = (this as AccessorShaderGroup).resourceManager.getResource(resourceLocation)
            val jsonobject = if(resourceLocation is ResourceLocationHandler) jsonparser.parse(resourceLocation.shader).asJsonObject else jsonparser.parse(IOUtils.toString(iresource.inputStream, StandardCharsets.UTF_8)).asJsonObject
            var j: Int
            var var8: Iterator<*>
            var jsonelement1: JsonElement
            val jsonexception2: JsonException
            var jsonarray1: JsonArray
            if (JsonUtils.isJsonArray(jsonobject, "targets")) {
                jsonarray1 = jsonobject.getAsJsonArray("targets")
                j = 0
                var8 = jsonarray1.iterator()
                while (var8.hasNext()) {
                    jsonelement1 = var8.next()
                    try {
                        initTargetHook(jsonelement1)
                    } catch (var18: Exception) {
                        jsonexception2 = JsonException.forException(var18)
                        jsonexception2.prependJsonKey("targets[$j]")
                        throw jsonexception2
                    }
                    ++j
                }
            }
            if (JsonUtils.isJsonArray(jsonobject, "passes")) {
                jsonarray1 = jsonobject.getAsJsonArray("passes")
                j = 0
                var8 = jsonarray1.iterator()
                while (var8.hasNext()) {
                    jsonelement1 = var8.next()
                    try {
                        parsePass(textureManager, jsonelement1)
                    } catch (var17: Exception) {
                        jsonexception2 = JsonException.forException(var17)
                        jsonexception2.prependJsonKey("passes[$j]")
                        throw jsonexception2
                    }
                    ++j
                }
            }
        } catch (var19: Exception) {
            val jsonexception = JsonException.forException(var19)
            jsonexception.setFilenameAndFlush(if(resourceLocation is ResourceLocationHandler) resourceLocation.name else resourceLocation.resourcePath)
            throw jsonexception
        } finally {
            IOUtils.closeQuietly(iresource)
        }
    }

    @Throws(JsonException::class, IOException::class)
    private fun parsePass(p_152764_1_: TextureManager, json: JsonElement) {
        val jsonobject = JsonUtils.getJsonObject(json, "pass")
        val s = JsonUtils.getString(jsonobject, "name")
        val s1 = JsonUtils.getString(jsonobject, "intarget")
        val s2 = JsonUtils.getString(jsonobject, "outtarget")
        val framebuffer = getFramebufferHook(s1)
        val framebuffer1 = getFramebufferHook(s2)
        if (framebuffer == null) {
            throw JsonException("Input target '$s1' does not exist")
        } else if (framebuffer1 == null) {
            throw JsonException("Output target '$s2' does not exist")
        } else {
            val shader = addShader(s, framebuffer, framebuffer1)
            val jsonarray = JsonUtils.getJsonArray(jsonobject, "auxtargets", null as JsonArray?)
            if (jsonarray != null) {
                var i = 0
                val var12: Iterator<*> = jsonarray.iterator()
                while (var12.hasNext()) {
                    val jsonelement = var12.next() as JsonElement
                    try {
                        val jsonobject1 = JsonUtils.getJsonObject(jsonelement, "auxtarget")
                        val s4 = JsonUtils.getString(jsonobject1, "name")
                        val s3 = JsonUtils.getString(jsonobject1, "id")
                        val framebuffer2 = getFramebufferHook(s3)
                        if (framebuffer2 == null) {
                            val rl = ResourceLocation.splitObjectName(s3)
                            val resourcelocation = ResourceLocation(rl[0], "textures/effect/" + rl[1] + ".png")
                            var iresource: IResource? = null
                            iresource = try {
                                resourceManager.getResource(resourcelocation)
                            } catch (var30: FileNotFoundException) {
                                throw JsonException("Render target or texture '$s3' does not exist")
                            } finally {
                                IOUtils.closeQuietly(iresource)
                            }
                            p_152764_1_.bindTexture(resourcelocation)
                            val lvt_20_2_ = p_152764_1_.getTexture(resourcelocation)
                            val lvt_21_1_ = JsonUtils.getInt(jsonobject1, "width")
                            val lvt_22_1_ = JsonUtils.getInt(jsonobject1, "height")
                            val var24 = JsonUtils.getBoolean(jsonobject1, "bilinear")
                            if (var24) {
                                GlStateManager.glTexParameteri(3553, 10241, 9729)
                                GlStateManager.glTexParameteri(3553, 10240, 9729)
                            } else {
                                GlStateManager.glTexParameteri(3553, 10241, 9728)
                                GlStateManager.glTexParameteri(3553, 10240, 9728)
                            }
                            shader.addAuxFramebuffer(s4, lvt_20_2_.glTextureId, lvt_21_1_, lvt_22_1_)
                        } else {
                            shader.addAuxFramebuffer(
                                s4,
                                framebuffer2,
                                framebuffer2.framebufferTextureWidth,
                                framebuffer2.framebufferTextureHeight
                            )
                        }
                    } catch (var32: java.lang.Exception) {
                        val jsonexception = JsonException.forException(var32)
                        jsonexception.prependJsonKey("auxtargets[$i]")
                        throw jsonexception
                    }
                    ++i
                }
            }
            val jsonarray1 = JsonUtils.getJsonArray(jsonobject, "uniforms", null as JsonArray?)
            if (jsonarray1 != null) {
                var l = 0
                val var35: Iterator<*> = jsonarray1.iterator()
                while (var35.hasNext()) {
                    val jsonelement1 = var35.next() as JsonElement
                    try {
                        initUniformHook(jsonelement1)
                    } catch (var29: java.lang.Exception) {
                        val jsonexception1 = JsonException.forException(var29)
                        jsonexception1.prependJsonKey("uniforms[$l]")
                        throw jsonexception1
                    }
                    ++l
                }
            }
        }
    }

    private fun getFramebufferHook(string: String?): Framebuffer? {
        return if (string == null) {
            null
        } else {
            if (string == "minecraft:main") this.mainFramebuffer else getFramebufferRaw(string)
        }
    }

    private fun initTargetHook(element : JsonElement) {
        if (JsonUtils.isString(element)) {
            addFramebuffer(element.asString, mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight)
        } else {
            val jsonobject = JsonUtils.getJsonObject(element, "target")
            val s = JsonUtils.getString(jsonobject, "name")
            val i = JsonUtils.getInt(jsonobject, "width", mainFramebuffer.framebufferWidth)
            val j = JsonUtils.getInt(jsonobject, "height", mainFramebuffer.framebufferHeight)
            if (getFramebufferRaw(s) != null) {
                throw JsonException("$s is already defined")
            }
            addFramebuffer(s, i, j)
        }
    }

    @Throws(JsonException::class)
    private fun initUniformHook(json: JsonElement) {
        val jsonobject = JsonUtils.getJsonObject(json, "uniform")
        val s = JsonUtils.getString(jsonobject, "name")
        val shaderuniform = ((this as AccessorShaderGroup).listShaders[listShaders.size - 1] as Shader).shaderManager.getShaderUniform(s)
        if (shaderuniform == null) {
            throw JsonException("Uniform '$s' does not exist")
        } else {
            val afloat = FloatArray(4)
            var i = 0
            val var7: Iterator<*> = JsonUtils.getJsonArray(jsonobject, "values").iterator()
            while (var7.hasNext()) {
                val jsonelement = var7.next() as JsonElement
                try {
                    afloat[i] = JsonUtils.getFloat(jsonelement, "value")
                } catch (var11: java.lang.Exception) {
                    val jsonexception = JsonException.forException(var11)
                    jsonexception.prependJsonKey("values[$i]")
                    throw jsonexception
                }
                ++i
            }
            when (i) {
                0 -> {
                }
                1 -> shaderuniform.set(afloat[0])
                2 -> shaderuniform[afloat[0]] = afloat[1]
                3 -> shaderuniform[afloat[0], afloat[1]] = afloat[2]
                4 -> shaderuniform[afloat[0], afloat[1], afloat[2]] = afloat[3]
                else -> {
                }
            }
        }
    }
}