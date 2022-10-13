package com.kisman.cc.util.render

import com.kisman.cc.util.render.konas.TessellatorUtil
import com.kisman.cc.util.render.objects.world.Vectors
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11

/**
 * @author _kisman_
 * @since 19:30 of 07.07.2022
 */
object RenderUtil3 {
    fun drawBox(
        vectors : Vectors,
        alpha: Int
    ) {
        GlStateManager.disableAlpha()
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        for(vec in vectors.vectors) {
            TessellatorUtil.colorVertex(vec.vec.x, vec.vec.y, vec.vec.z, vec.color, alpha, bufferbuilder)
        }

        tessellator.draw()
        GlStateManager.enableAlpha()
    }

    fun drawBoundingBox(
        vectors : Vectors,
        width : Double,
        alpha : Int
    ) {
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        GlStateManager.glLineWidth(width.toFloat())
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)

        for(vec in vectors.vectors) {
            TessellatorUtil.colorVertex(vec.vec.x, vec.vec.y, vec.vec.z, vec.color, alpha, bufferbuilder)
        }

        tessellator.draw()
    }

    fun toAABB(
        aabb : AxisAlignedBB,
        side : EnumFacing
    ) : AxisAlignedBB {
        return when (side) {
            EnumFacing.DOWN -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ)
            EnumFacing.UP -> AxisAlignedBB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.NORTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ)
            EnumFacing.SOUTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.WEST -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ)
            EnumFacing.EAST -> AxisAlignedBB(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
        }
    }
}