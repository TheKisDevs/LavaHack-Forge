package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Addon
import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 14:05 of 08.08.2022
 */
@Addon("AutoRer")
@Beta
object Crystals : Module(
    "Crystals",
    "Changes place of of AutoRer if he have no valid place pos. Useful for breaking surround.",
    Category.COMBAT
) {
    private val debug = register(Setting("Debug", this, true))

    var pos : BlockPos? = null
    var state = false

    override fun onEnable() {
        super.onEnable()
        reset()
    }

    override fun onDisable() {
        super.onDisable()
        reset()
    }

    fun reset() {
        pos = null
        state = false
    }

    @SubscribeEvent fun onLeftClickBlock(event : PlayerInteractEvent.LeftClickBlock) {
        val block = mc.world.getBlockState(event.pos)
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            if(debug.valBoolean) {
                println("meow2 its obby or bebrock")
            }
            pos = event.pos
            state = true
        }
    }
}