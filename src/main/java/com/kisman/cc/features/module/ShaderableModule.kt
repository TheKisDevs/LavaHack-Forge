package com.kisman.cc.features.module

import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.client.interfaces.Drawable
import java.util.function.Supplier

/**
 * Easier implementation of shader render
 *
 * Note: Only for simple render modules like BlockHighlight
 *
 * @author _kisman_
 * @since 19:05 of 27.01.2023
 */
abstract class ShaderableModule(
    private val hasFlags : Boolean = false
) : Module(), Drawable {
    constructor(
        name : String,
        desc : String = "",
        category : Category,
        hasFlags : Boolean = false
    ) : this(
        hasFlags
    ) {
        super.setName(name)
        super.setDescription(desc)
        super.category = category
    }

    private val defaultFlags = mutableListOf<Supplier<Boolean>>()

    fun addFlag(
        supplier : Supplier<Boolean>
    ) {
        defaultFlags.add(supplier)
    }

    fun addFlags(
        vararg suppliers : Supplier<Boolean>
    ) {
        for(supplier in suppliers) {
            addFlag(supplier)
        }
    }

    fun handleDraw(
        pattern : RenderingRewritePattern
    ) {
        if(pattern.canRender()) {
            draw()
        }
    }

    fun handleDraw(
        vararg flags : Boolean
    ) {
        if(flags.isEmpty() && !hasFlags) {
            draw()
        } else {
            val currentFlags = if(defaultFlags.isEmpty()) {
                flags.toTypedArray()
            } else {
                val values = mutableListOf<Boolean>()

                for(flag in defaultFlags) {
                    values.add(flag.get())
                }

                values.toTypedArray()
            }

            draw0(currentFlags)
        }
    }

    /**
     * Only when we have default flags
     */
    fun handleDrawShadered() {
        if(hasFlags) {
            val values = mutableListOf<Boolean>()

            for (flag in defaultFlags) {
                values.add(!flag.get())
            }

            draw0(values.toTypedArray())
        } else {
            draw()
        }
    }

    open fun draw0(
        flags : Array<Boolean>
    ) { }

    override fun draw() { }
}