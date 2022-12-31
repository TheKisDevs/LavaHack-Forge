package com.kisman.cc.util.math.vectors.bb

import com.kisman.cc.util.Colour
import net.minecraft.util.math.AxisAlignedBB

/**
 * @author _kisman_
 * @since 12:42 of 31.12.2022
 */
class ColorableSlideBB(
    val bb : AxisAlignedBB,
    val colour1 : Colour,
    val colour2 : Colour,
    val colour3 : Colour,
    val colour4 : Colour,
    val colour5 : Colour,
    val colour6 : Colour
) : AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ)