package com.kisman.cc.util.enums

import com.kisman.cc.settings.util.ShadersRendererPattern
import com.kisman.cc.settings.util.shadercharmsrewrite.ShaderCharmsRewriteUniform
import com.kisman.cc.util.math.vectors.Vec3f
import com.kisman.cc.util.render.shader.framebuffer.FramebufferShader
import com.kisman.cc.util.render.shader.resolution
import com.kisman.cc.util.render.shader.resolutionUniform
import com.kisman.cc.util.render.shader.texelUniform
import com.kisman.cc.util.render.shader.textureUniform
import com.kisman.cc.util.render.shader.uniform.Uniform
import com.kisman.cc.util.render.shader.uniform.type.types.*

/**
 * @author _kisman_
 * @since 14:46 of 16.08.2022
 */
enum class ShadersShaders(
    val name_ : String,
    val displayName : String,
    val uniforms : List<ShaderCharmsRewriteUniform<*>>,
    val animated : Boolean,
    val haveTextureUniform : Boolean,
    val haveTexelSizeUniform : Boolean,
    val haveResolutionUniform : Boolean,
    val index : Int,
    val radiusIndex : Int,
    val qualityIndex : Int
) {
    ItemGlow(
        "itemglow.frag",
        "Item Glow",
        listOf(
            ShaderCharmsRewriteUniform(null, textureUniform(), 0),
            ShaderCharmsRewriteUniform(null, texelUniform(1f, 1f), 1),
            ShaderCharmsRewriteUniform(null, resolutionUniform(), 2),
            ShaderCharmsRewriteUniform(null, Uniform<TypeImage>("image").set(TypeImage()), 3),
            ShaderCharmsRewriteUniform("Color", Uniform<TypeVec3Float>("color").set(TypeVec3Float().set(Vec3f(1f, 1f, 1f))), 4),
            ShaderCharmsRewriteUniform("Divider", Uniform<TypeFloat>("divider").set(TypeFloat().set(140f)), 5),
            ShaderCharmsRewriteUniform("Radius", Uniform<TypeFloat>("radius").set(TypeFloat().set(1f)), 6),
            ShaderCharmsRewriteUniform("Max Sample", Uniform<TypeFloat>("maxSample").set(TypeFloat().set(10f)), 7),
            ShaderCharmsRewriteUniform("Blur", Uniform<TypeBool>("blur").set(TypeBool()), 8),
            ShaderCharmsRewriteUniform("Mix", Uniform<TypeFloat>("mixFactor").set(TypeFloat()), 9),
            ShaderCharmsRewriteUniform("Alpha", Uniform<TypeFloat>("minAlpha").set(TypeFloat()), 10),
            ShaderCharmsRewriteUniform("Image Mix", Uniform<TypeFloat>("imageMix").set(TypeFloat()), 11),
            ShaderCharmsRewriteUniform("Use Image", Uniform<TypeBool>("useImage").set(TypeBool()), 12)
        ),
        false,
        true,
        true,
        true,
        0,
        6,
        -1
    ) {
        override fun updateUniforms(
            pattern : ShadersRendererPattern,
            framebuffer : FramebufferShader
        ) {

        }
    },
    Snow(
        "snow.frag",
        "Snow",
        listOf(
            ShaderCharmsRewriteUniform(null, textureUniform(), 0),
            ShaderCharmsRewriteUniform(null, Uniform<TypeFloat>("time").set(TypeFloat()), 1),
            ShaderCharmsRewriteUniform(null, resolutionUniform(), 2),
            ShaderCharmsRewriteUniform("Mouse", Uniform<TypeFloat>("mouse").set(TypeFloat()), 3)
        ),
        true,
        true,
        false,
        true,
        1,
        -1,
        -1
    ) {
        @Suppress("UNCHECKED_CAST")
        override fun updateUniforms(
            pattern : ShadersRendererPattern,
            framebuffer : FramebufferShader
        ) {
            if(pattern.uniforms.contains(index)) {
                val bind = pattern.uniforms[index]

                for(uniform in uniforms) {
                    if(uniform.settingName != null) {
                        if(bind?.second?.contains(uniform.index)!!) {
                            val setting = bind.second[uniform.index]!!

                            if(uniform.get() is TypeInt) {
                                (uniform.get() as TypeInt).set(setting.setting.valInt)
                            } else if(uniform.get() is TypeFloat) {
                                (uniform.get() as TypeFloat).set(setting.setting.valFloat)
                            } else if(uniform.get() is TypeBool) {
                                (uniform.get() as TypeBool).set(setting.setting.valBoolean)
                            }

                            uniform.get().setup(framebuffer.uniformsRaw[uniform.name]!!)
                        }
                    } else {
                        if(haveResolutionUniform && uniform.name == "resolution") {
                            resolution(uniform as Uniform<TypeVec2Float>)
                        } else if(haveTexelSizeUniform && uniform.name == "texelSize") {
                            texelUniform(
                                uniform as Uniform<TypeVec2Float>,
                                (
                                        if(radiusIndex == -1) {
                                            1f
                                        } else {
                                            (uniforms[radiusIndex].get() as TypeFloat).get()
                                        }
                                ),
                                (
                                        if(qualityIndex == -1) {
                                            1f
                                        } else {
                                            (uniforms[qualityIndex].get() as TypeFloat).get()
                                        }
                                )
                            )
                        } else if(uniform.name == "time") {
                            //TODO
                        }
                    }
                }
            }
        }
    };

    abstract fun updateUniforms(
        pattern : ShadersRendererPattern,
        framebuffer : FramebufferShader
    )

    open fun setupUniforms(
        pattern : ShadersRendererPattern,
        framebuffer : FramebufferShader
    ) {
        for(uniform in uniforms) {
            framebuffer.setupUniform(uniform.name)
        }
    }
}