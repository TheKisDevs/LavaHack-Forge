package com.kisman.cc.gui.api.shaderable

import com.kisman.cc.gui.api.ComponentImplementation
import com.kisman.cc.gui.api.Shaderable
import com.kisman.cc.util.client.collections.Bind

/**
 * @author _kisman_
 * @since 18:33 of 19.01.2023
 */
abstract class ShaderableImplementation(
    x : Int,
    y : Int,
    count : Int,
    offset : Int,
    layer : Int
) : ComponentImplementation(
    x,
    y,
    count,
    offset,
    layer
),
    Shaderable {
    @JvmField protected var normalRender = Runnable { }
    @JvmField protected var shaderRender = Bind(Runnable { }, Runnable { })

    override fun normalRender() : Runnable = normalRender

    override fun shaderRender() : Bind<Runnable, Runnable> = shaderRender
}