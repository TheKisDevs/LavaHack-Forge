package com.kisman.cc.util.render.objects.world

import net.minecraft.util.math.*

class Box(
    var pos : Vec3d,
    var size : Vec3d
    ) {
    constructor(
        box : Box
    ) : this(box.pos, box.size)
    constructor(
        aabb : AxisAlignedBB
    ) : this(byAABB(aabb))

    fun toAABB() : AxisAlignedBB = AxisAlignedBB(pos, getMaxBySize(pos, size))

    fun center() : Vec3d = Vec3d(
        pos.x + size.x / 2,
        pos.y + size.y / 2,
        pos.z + size.z / 2
    )

    companion object {
        @JvmStatic
        fun getMaxBySize(
            pos : Vec3d,
            size : Vec3d
        ) : Vec3d = Vec3d(pos.x + size.x, pos.y + size.y, pos.z + size.z)

        @JvmStatic
        fun byAABB(
            aabb : AxisAlignedBB
        ) : Box = Box(Vec3d(aabb.minX, aabb.minY, aabb.minZ), Vec3d(aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ))
    }
}