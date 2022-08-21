package com.kisman.cc.util.render.shader.uniform.type

import com.kisman.cc.util.render.shader.uniform.Uniform

/**
 * @author _kisman_
 * @since 14:56 of 16.08.2022
 */
@Suppress("UNCHECKED_CAST", "LeakingThis")
abstract class Type<T, R> {
    private var t : T = getDefault()

    abstract fun getDefault() : T
    abstract fun setup(uniform : Int) : R

    fun get() : T {
        return t!!
    }

    fun set(t : T) : R {
        this.t = t

        return this as R
    }
}