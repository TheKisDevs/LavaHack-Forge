package com.kisman.cc.features.module.combat.cityboss

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.render.left
import com.kisman.cc.util.render.right
import com.kisman.cc.util.world.BlockUtil
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * `BlockPos(0, 0, 0) or BlockPos.ORIGIN` is the center of the player's position
 *
 * @author _kisman_
 * @since 16:46 of 118.10.2022
 */
enum class Cases {
    MiddleCase {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                BlockPos.ORIGIN.offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing).up()
            )
        }
    },
    SimpleCase1 {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                BlockPos.ORIGIN.offset(facing),
                BlockPos.ORIGIN.offset(facing).up()
            )
        }
    },
    SimpleCase2 {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                BlockPos.ORIGIN.offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing).offset(facing),
                `1-13-BlockPos`(BlockPos.ORIGIN.offset(facing).offset(facing).offset(facing).up())
            )
        }
    },
    LeftDiagonalCase {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> = listOf<BlockPos>(
            BlockPos.ORIGIN.offset(facing),
            BlockPos.ORIGIN.offset(facing).offset(facing.left()),
            BlockPos.ORIGIN.offset(facing).offset(facing.left()).offset(facing),
            `1-13-BlockPos`(BlockPos.ORIGIN.offset(facing).offset(facing.left()).offset(facing).up())
        )
    },
    RightDiagonalCase {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> = listOf<BlockPos>(
            BlockPos.ORIGIN.offset(facing),
            BlockPos.ORIGIN.offset(facing).offset(facing.right()),
            BlockPos.ORIGIN.offset(facing).offset(facing.right()).offset(facing),
            `1-13-BlockPos`(BlockPos.ORIGIN.offset(facing).offset(facing.right()).offset(facing).up())
        )
    }
    ;

    abstract fun posses(
        facing : EnumFacing
    ) : List<BlockPos>

    fun howManyAirs(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean
    ) : Int {
        var airs = 0

        for(pos1 in posses(facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if (mc.world.getBlockState(pos2).block == Blocks.AIR) {
                    airs++
                }
            }
        }

        return airs
    }

    fun howManyAirs(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean,
        down : Int
    ) : Int {
        var airs = 0

        for(pos1 in down(down, facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if(mc.world.getBlockState(pos2).block == Blocks.AIR) {
                    airs++
                }
            }
        }

        return airs
    }

    fun isItInRange(
        facing : EnumFacing,
        pos : BlockPos,
        range : Double,
        newVersion : Boolean
    ) : Boolean {
        for(pos1 in posses(facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if (mc.player.getDistanceSq(pos2) > (range * range)) {
                    return false
                }
            }
        }

        return true
    }

    fun isItInRange(
        facing : EnumFacing,
        pos : BlockPos,
        range : Double,
        newVersion : Boolean,
        down : Int
    ) : Boolean {
        for(pos1 in down(down, facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if (mc.player.getDistanceSq(pos2) > (range * range)) {
                    return false
                }
            }
        }

        return true
    }

    fun isIt(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean
    ) : Boolean {
        for(pos1 in posses(facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if (mc.world.getBlockState(pos2).block != Blocks.AIR && !BlockUtil.canBlockBeBroken(pos2)) {
                    return false
                }
            }
        }

        return true
    }

    fun isIt(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean,
        down : Int
    ) : Boolean {
        for(pos1 in down(down, facing)) {
            if((newVersion && pos1 !is `1-13-BlockPos`) || !newVersion) {
                val pos2 = pos.add(pos1)

                if (mc.world.getBlockState(pos2).block != Blocks.AIR && !BlockUtil.canBlockBeBroken(pos2)) {
                    return false
                }
            }
        }

        return true
    }

    fun down(
        n : Int,
        facing : EnumFacing
    ) : List<BlockPos> {
        val posses = ArrayList<BlockPos>()

        for(pos in posses(facing)) {
            posses.add(pos.down(n))
        }

        return posses
    }
}