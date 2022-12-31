package com.kisman.cc.util.math.vectors.bb

import com.kisman.cc.util.Colour
import net.minecraft.util.math.AxisAlignedBB

/**
 * @author _kisman_
 * @since 12:42 of 31.12.2022
 */
class ColorableBB(
    val bb : AxisAlignedBB,
    val color : Colour
) : AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ)