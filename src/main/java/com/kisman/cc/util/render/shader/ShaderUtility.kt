package com.kisman.cc.util.render.shader

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.render.shader.uniform.Uniform
import com.kisman.cc.util.render.shader.uniform.type.types.TypeInt
import com.kisman.cc.util.render.shader.uniform.type.types.TypeVec2Float
import com.kisman.cc.util.sr
import net.minecraft.util.math.Vec2f

/**
 * @author _kisman_
 * @since 15:55 of 16.08.2022
 */

fun textureUniform() : Uniform<TypeInt> = Uniform<TypeInt>("texture").set(TypeInt())

fun texelUniform(
    radius : Float,
    quality : Float
) : Uniform<TypeVec2Float> {
    return Uniform<TypeVec2Float>("texelSize").set(TypeVec2Float().set(Vec2f(
        1f / mc.displayWidth * (radius * quality),
        1f / mc.displayHeight * (radius * quality)
    )))
}

fun texelUniform(
    uniform : Uniform<TypeVec2Float>,
    radius : Float,
    quality : Float
) : Uniform<TypeVec2Float> {
    uniform.get().set(Vec2f(
        1f / mc.displayWidth * (radius * quality),
        1f / mc.displayHeight * (radius * quality)
    ))

    return uniform
}

fun resolution(
    uniform : Uniform<TypeVec2Float>
) : Uniform<TypeVec2Float> {
    uniform.get().set(Vec2f(
        sr().scaledWidth.toFloat(),
        sr().scaledHeight.toFloat()
    ))

    return uniform
}

fun resolutionUniform() : Uniform<TypeVec2Float> {
    return Uniform<TypeVec2Float>("resolution").set(TypeVec2Float().set(Vec2f(
        sr().scaledWidth.toFloat(),
        sr().scaledHeight.toFloat()
    )))
}