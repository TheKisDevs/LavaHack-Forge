package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

/**
 * WIP
 *
 * @author _kisman_
 * @since 19:16 of 05.09.2022
 */
class AutoMine : Module(
    "AutoMine",
    "Automatically mines surround and burrow blocks of your target",
    Category.COMBAT
) {
    private val burrow = register(Setting("Burrow", this, false))
    private val surround = register(Setting("Surround", this, false))

    override fun onEnable() {
        super.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        //TODO: custom target finder
        val target = AutoRer.currentTarget ?: return

        val trapped = isTrapped(
            target
        )

        val posToMine = if(trapped) {
            target.position
        } else {
            getSurroundBlock()
        }

        mineBlock(
            posToMine
        )
    }

    private fun mineBlock(
        pos : BlockPos
    ) {

    }

    private fun getSurroundBlock(

    ) : BlockPos {
        return BlockPos(0, 0, 0)//TODO: change it
    }

    private fun canBeInBurrow(
        player : EntityPlayer
    ) : Boolean {
        return true//TODO: change it
    }

    private fun isTrapped(
        player : EntityPlayer
    ) : Boolean {
        val blockPos = player.position.up(2)
        val block = mc.world.getBlockState(blockPos)
        return block.block != Blocks.AIR
    }
}