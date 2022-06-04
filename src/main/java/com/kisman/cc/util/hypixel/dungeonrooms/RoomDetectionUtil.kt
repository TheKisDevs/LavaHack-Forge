package com.kisman.cc.util.hypixel.dungeonrooms

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos

/**
 * Thanks to https://github.com/Quantizr/DungeonRoomsMod/blob/3.x/src/main/java/io/github/quantizr/dungeonrooms/utils/RoomDetectionUtils.java
 *
 * @author _kisman_
 * @since 10:16 of 01.06.2022
 */
class RoomDetectionUtil {
    companion object {
        private val mc = Minecraft.getMinecraft()
        
        val whitelistedBlocks = setOf(
            //These are the blocks which are stored in the ".skeleton" files
            100, //Stone
            103, //Diorite
            104, //Polished Diorite
            105, //Andesite
            106, //Polished Andesite
            200, //Grass
            300, //Dirt
            301, //Coarse Dirt
            400, //Cobblestone
            700, //Bedrock
            1800, //Oak Leaves
            3507, //Gray Wool
            4300, //Double Stone Slab
            4800, //Mossy Cobblestone
            8200, //Clay
            9800, //Stone Bricks
            9801, //Mossy Stone Bricks
            9803, //Chiseled Stone Bricks
            15907, //Gray Stained Clay
            15909, //Cyan Stained Clay
            15915 //Black Stained Clay
        )
        
        fun getID(pos : BlockPos) : Int {
            return Block.getIdFromBlock(mc.world.getBlockState(pos).block) * 100 + mc.world.getBlockState(pos).block.damageDropped(mc.world.getBlockState(pos))
        }
    }
}