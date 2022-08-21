package com.kisman.cc.util.render.shader.uniform

import com.kisman.cc.util.render.shader.uniform.type.Type
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 14:48 of 16.08.2022
 */
open class Uniform<T : Type<*, *>>(
    val name : String
) {
    private var t : T? = null

    fun set(t : T) : Uniform<T> {
        this.t = t

        return this
    }

    fun get() : T = t!!
}