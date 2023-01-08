package com.kisman.cc.util.math

import net.minecraft.util.math.Vec2f

/**
 * @author _kisman_
 * @since 14:04 of 08.01.2023
 */

fun project(
    fov : Float,
    pointX : Float,
    pointY : Float,
    pointZ : Float
) : Vec2f = Vec2f(project(fov, pointX, pointZ), project(fov, pointY, pointZ))

fun project(
    fov : Float,
    point1 : Float,
    point2 : Float
) : Float = (point1 * fov) / point2