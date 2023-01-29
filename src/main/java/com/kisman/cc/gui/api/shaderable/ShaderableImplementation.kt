package com.kisman.cc.gui.api.shaderable

import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.api.Shaderable
import com.kisman.cc.util.client.collections.Bind

/**
 * @author _kisman_
 * @since 18:33 of 19.01.2023
 */
abstract class ShaderableImplementation : Component, Shaderable {
    @JvmField protected var normalRender = Runnable { }
    @JvmField protected var shaderRender = Bind(Runnable { }, Runnable { })

    override fun normalRender() : Runnable = normalRender

    override fun shaderRender() : Bind<Runnable, Runnable> = shaderRender
}