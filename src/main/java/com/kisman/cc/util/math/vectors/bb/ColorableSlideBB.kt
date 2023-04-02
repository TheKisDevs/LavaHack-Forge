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
    val colour6 : Colour,
    val colour7 : Colour,
    val colour8 : Colour,
    val colour9 : Colour,
    val colour10 : Colour,
    val colour11 : Colour,
    val colour12 : Colour,
    val colour13 : Colour,
    val colour14 : Colour,
    val colour15 : Colour,
    val colour16 : Colour
) : AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ)