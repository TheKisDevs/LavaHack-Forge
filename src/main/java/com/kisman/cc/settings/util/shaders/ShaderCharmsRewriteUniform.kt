package com.kisman.cc.settings.util.shaders

import com.kisman.cc.util.render.shader.uniform.Uniform
import com.kisman.cc.util.render.shader.uniform.type.Type

/**
 * @author _kisman_
 * @since 15:26 of 16.08.2022
 */
class ShaderCharmsRewriteUniform<T : Type<*, *>>(
    val settingName : String?,
    name : String,
    val index : Int
) : Uniform<T>(
    name
) {
    fun radius() : Boolean = name == "radius"

    constructor(
        settingName : String?,
        uniform : Uniform<T>,
        index : Int
    ) : this(
        settingName,
        uniform.name,
        index
    ) {
        set(uniform.get())
    }
}