package com.kisman.cc.util.enums

import com.kisman.cc.util.client.collections.Triple

/**
 * @author _kisman_
 * @since 17:42 of 03.02.2023
 */
enum class DirectionVertexes(
    @JvmField val axis : Axes,
    @JvmField vararg val vertexes : Triple<AxisValues>
) {
    Xp(
        Axes.Xp,
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MinZ),
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MinZ)
    ),
    Yp(
        Axes.Yp,
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MinZ),
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MinZ)
    ),
    Zp(
        Axes.Zp,
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MaxZ),
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MaxZ)
    ),

    Xm(
        Axes.Xm,
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MinZ),
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MaxZ),
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MaxZ),
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MinZ)
    ),
    Ym(
        Axes.Ym,
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MinZ),
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MaxZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MinZ)
    ),
    Zm(
        Axes.Zm,
        Triple(AxisValues.MinX, AxisValues.MaxY, AxisValues.MinZ),
        Triple(AxisValues.MaxX, AxisValues.MaxY, AxisValues.MinZ),
        Triple(AxisValues.MaxX, AxisValues.MinY, AxisValues.MinZ),
        Triple(AxisValues.MinX, AxisValues.MinY, AxisValues.MinZ)
    )

    ;

    private fun axisValueOf(
        vertex : Triple<AxisValues>,
        direction : Directions
    ) : AxisValues = when(direction) {
        Directions.X -> vertex.first
        Directions.Y -> vertex.second
        Directions.Z -> vertex.third
    }

    fun valueOf(
        vertex : Triple<AxisValues>,
        direction : Directions,
        min : Double,
        max : Double
    ) : Double = if(axisValueOf(vertex, direction).limit == Limits.Min) {
        min
    } else {
        max
    }

    fun valueOf(
        vertex : Int,
        direction : Directions,
        min : Double,
        max : Double
    ) : Double = valueOf(
        vertexes[vertex],
        direction,
        min,
        max
    )

    enum class Axes {
        Xp,
        Yp,
        Zp,

        Xm,
        Ym,
        Zm
    }

    enum class Limits {
        Min,
        Max
    }

    enum class Directions {
        X,
        Y,
        Z
    }

    enum class AxisValues(
        val direction : Directions,
        val limit : Limits
    ) {
        MinX(Directions.X, Limits.Min),
        MaxX(Directions.X, Limits.Max),

        MinY(Directions.Y, Limits.Min),
        MaxY(Directions.Y, Limits.Max),

        MinZ(Directions.Z, Limits.Min),
        MaxZ(Directions.Z, Limits.Max)
    }
}