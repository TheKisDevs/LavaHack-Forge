package com.kisman.cc.util.math

import com.kisman.cc.util.Globals.mc
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author _kisman_
 * @since 20:33 of 16.07.2022
 */

//Fixed coerceIn method
fun Double.coerceIn(minimumValue : Double, maximumValue : Double) : Double {
    if (this < minimumValue) return minimumValue
    if (this > maximumValue) return maximumValue
    return this
}

//Fixed coerceIn method
fun Float.coerceIn(minimumValue : Float, maximumValue : Float) : Float {
    if (this < minimumValue) return minimumValue
    if (this > maximumValue) return maximumValue
    return this
}

fun Float.max(second : Float) : Float {
    return kotlin.math.max(this, second)
}

fun Int.max(second : Int) : Int {
    return kotlin.math.max(this, second)
}

fun Double.max(second : Double) : Double {
    return kotlin.math.max(this, second)
}

fun lerp(
    from : Double,
    to : Double,
    delta : Double
) : Double {
    return from + (to - from) * delta
}

fun lerp(
    from : Int,
    to : Int,
    delta : Int
) : Int {
    return from + (to - from) * delta
}

fun Double.min(
    minimumValue : Double
) : Double {
    return kotlin.math.min(
        this,
        minimumValue
    )
}

fun Double.square() : Double = this * this

fun toDelta(start : Long) : Long = System.currentTimeMillis() - start

fun toDelta(start : Long, length : Float) : Float = (toDelta(start).toFloat() / length).coerceIn(0.0f, 1.0f)

fun processFastFunction(
    map : HashMap<Float, Float>,
    getter : (Float) -> Float,
    `value` : Float
) : Float = if(map.containsKey(`value`)) {
    map[`value`]!!
} else {
    getter(`value`).also { map[`value`] = it }
}

private val sqrtMap = HashMap<Float/*squared number*/, Float/*non squared number*/>()

fun sqrt2(
    squared : Float
) : Float = processFastFunction(sqrtMap, { sqrt(it) }, squared)

fun sqrt2(
    squared : Double
) : Double = processFastFunction(sqrtMap, { sqrt(it) }, squared.toFloat()).toDouble()

fun sqrt(
    squared : Float
) : Float = sqrt(squared.toDouble()).toFloat()

fun hypot(
    x : Double,
    y : Double
) : Double = sqrt2(x * x + y * y)

fun round(
    value : Float,
    places : Int
) = BigDecimal.valueOf(value.toDouble()).setScale(abs(places), RoundingMode.FLOOR).toFloat()

fun absNormalize(
    a : Float,
    bound : Float
) = (a % bound + bound) % bound

fun clamp(
    num : Double,
    min : Double,
    max : Double
) = if(num < min) {
    min
} else {
    num.coerceAtMost(max)
}

fun clamp(
    num : Float,
    min : Float,
    max : Float
) = clamp(num.toDouble(), min.toDouble(), max.toDouble()).toFloat()

fun interpolate(
    entity : Entity,
    time : Float
) = Vec3d(
    entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
    entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
    entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time
)

fun interpolated(
    entity : Entity,
    time : Float
) = interpolate(entity, time).subtract(mc.renderManager.viewerPosX, mc.renderManager.viewerPosY, mc.renderManager.viewerPosZ)!!

fun curve(
    a : Double
) = sqrt2(1.0 - (a - 1.0) * (a - 1.0))

const val ALMOST_ZERO = 1e-4f

fun isAlmostZero(
    value : Float,
    tolerance : Float
) = abs(value) <= tolerance

fun interpolateTo(
    current : Float,
    target : Float,
    time : Float,
    speed : Float
) : Float {
    val distance = target - current

    return if(speed <= 0 || isAlmostZero(distance * distance, ALMOST_ZERO)) {
        target
    } else {
        val delta = distance * clamp(time * speed, 0f, 1f)

        return current + delta
    }
}

fun distance(
    x1 : Int,
    y1 : Int,
    z1 : Int,
    x2 : Int,
    y2 : Int,
    z2 : Int
) : Double {
    return sqrt2(distanceSq(x1, y1, z1, x2, y2, z2))
}

fun distance(
    x1 : Double,
    y1 : Double,
    z1 : Double,
    x2 : Double,
    y2 : Double,
    z2 : Double
) : Double {
    return sqrt2(distanceSq(x1, y1, z1, x2, y2, z2))
}

fun distanceSq(
    x1 : Double,
    y1 : Double,
    z1 : Double,
    x2 : Double,
    y2 : Double,
    z2 : Double
) : Double = (x2 - x1).pow(2) + (y2 - y1).pow(2) + (z2 - z1).pow(2)

fun distanceSq(
    x1 : Int,
    y1 : Int,
    z1 : Int,
    x2 : Int,
    y2 : Int,
    z2 : Int
) : Double = (x2 - x1).toDouble().pow(2) + (y2 - y1).toDouble().pow(2) + (z2 - z1).toDouble().pow(2)

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