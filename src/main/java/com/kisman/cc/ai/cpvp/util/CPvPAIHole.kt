package com.kisman.cc.ai.cpvp.util

import com.kisman.cc.util.world.HoleUtil
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 */
class CPvPAIHole(
    val pos : BlockPos,
    val info : HoleUtil.HoleInfo,
    val type : HoleUtil.HoleType,
    val safety : HoleUtil.BlockSafety
)