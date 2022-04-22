package com.kisman.cc.util.render.objects

import net.minecraft.util.math.*

class Box(var pos: Vec3d, var size: Vec3d) {
    fun toAABB(): AxisAlignedBB {
        return AxisAlignedBB(pos, getMaxBySize(pos, size))
    }

    companion object {
        fun getMaxBySize(pos: Vec3d, size: Vec3d): Vec3d {
            return Vec3d(pos.x + size.x, pos.y + size.y, pos.z + size.z)
        }

        fun byAABB(aabb: AxisAlignedBB): Box {
            return Box(Vec3d(aabb.minX, aabb.minY, aabb.minZ), Vec3d(aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ))
        }
    }
}