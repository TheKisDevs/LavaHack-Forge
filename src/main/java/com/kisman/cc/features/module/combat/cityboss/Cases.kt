package com.kisman.cc.features.module.combat.cityboss

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.block
import com.kisman.cc.util.render.left
import com.kisman.cc.util.render.right
import com.kisman.cc.util.world.BlockUtil2
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * `BlockPos(0, 0, 0)` or `BlockPos.ORIGIN` is the center of the player's position
 *
 * @author _kisman_
 * @since 16:46 of 118.10.2022
 */
enum class Cases {
    MiddleCase {
        /**
         * TODO: make something like priority system
         *
         * TODO: middlecase is better if you are using automine and ca from future,
         * TODO: cuz block of surround can be broken with biggest chance
         *
         * TODO: (i will make our automine soon so not only from future)
         */
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                SurroundBlockPos(BlockPos.ORIGIN.offset(facing)),
                BlockPos.ORIGIN.offset(facing).offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing).up(),
                CrystalBlockPos(BlockPos.ORIGIN.offset(facing).offset(facing).down())
            )
        }
    },
    SimpleCase1 {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                SurroundBlockPos(BlockPos.ORIGIN.offset(facing)),
                `1-13-BlockPos`(BlockPos.ORIGIN.offset(facing).up()),
                CrystalBlockPos(BlockPos.ORIGIN.offset(facing).down())
            )
        }
    },
    SimpleCase2 {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> {
            return listOf<BlockPos>(
                SurroundBlockPos(BlockPos.ORIGIN.offset(facing)),
                BlockPos.ORIGIN.offset(facing).offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing).offset(facing),
                BlockPos.ORIGIN.offset(facing).offset(facing).offset(facing).up(),
                CrystalBlockPos(BlockPos.ORIGIN.offset(facing).offset(facing).offset(facing).down())
            )
        }
    },
    LeftDiagonalCase {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> = listOf<BlockPos>(
            SurroundBlockPos(BlockPos.ORIGIN.offset(facing)),
            BlockPos.ORIGIN.offset(facing).offset(facing.left()),
            BlockPos.ORIGIN.offset(facing).offset(facing.left()).offset(facing),
            BlockPos.ORIGIN.offset(facing).offset(facing.left()).offset(facing).up(),
            CrystalBlockPos(BlockPos.ORIGIN.offset(facing).offset(facing.left()).offset(facing).down())
        )
    },
    RightDiagonalCase {
        override fun posses(
            facing : EnumFacing
        ) : List<BlockPos> = listOf<BlockPos>(
            SurroundBlockPos(BlockPos.ORIGIN.offset(facing)),
            BlockPos.ORIGIN.offset(facing).offset(facing.right()),
            BlockPos.ORIGIN.offset(facing).offset(facing.right()).offset(facing),
            BlockPos.ORIGIN.offset(facing).offset(facing.right()).offset(facing).up(),
            CrystalBlockPos(BlockPos.ORIGIN.offset(facing).offset(facing.right()).offset(facing).down())
        )
    }
    ;

    abstract fun posses(
        facing : EnumFacing
    ) : List<BlockPos>

    private fun valid(
        pos : BlockPos,
        newVersion : Boolean
    ) : Boolean = pos !is CrystalBlockPos && ((newVersion && pos !is `1-13-BlockPos`) || !newVersion)

    fun howManyObbis(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean
    ) : Int {
        var obbis = 0

        for(pos1 in posses(facing)) {
            if(valid(pos1, newVersion)) {
                val pos2 = pos.add(pos1)

                if (block(pos2) != Blocks.AIR) {
                    obbis++
                }
            }
        }

        return obbis
    }

    fun howManyObbis(
        facing : EnumFacing,
        pos : BlockPos,
        newVersion : Boolean,
        down : Int
    ) : Int {
        var obbis = 0

        for(pos1 in down(down, facing)) {
            if(valid(pos1, newVersion)) {
                val pos2 = pos.add(pos1)

                if(block(pos2) != Blocks.AIR) {
                    obbis++
                }
            }
        }

        return obbis
    }

    fun isItInRange(
        facing : EnumFacing,
        pos : BlockPos,
        range : Double,
        newVersion : Boolean
    ) : Boolean {
        for(pos1 in posses(facing)) {
            if(valid(pos1, newVersion)) {
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
            if(valid(pos1, newVersion)) {
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
            if(valid(pos1, newVersion)) {
                val pos2 = pos.add(pos1)

                if (block(pos) == Blocks.BEDROCK || (block(pos2) != Blocks.AIR && !BlockUtil2.canBlockBeBroken(pos2))) {
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
            if(valid(pos1, newVersion)) {
                val pos2 = pos.add(pos1)

                if (block(pos) == Blocks.BEDROCK || (block(pos2) != Blocks.AIR && !BlockUtil2.canBlockBeBroken(pos2))) {
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

    fun distanceSq(
        facing : EnumFacing,
        centre : BlockPos
    ) : Double {
        for(offset in posses(facing)) {
            if(offset is SurroundBlockPos) {
                val pos = centre.add(offset)

                return mc.player.getDistanceSq(pos)
            }
        }

        return Double.NaN
    }
}