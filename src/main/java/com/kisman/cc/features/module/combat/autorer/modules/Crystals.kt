package com.kisman.cc.features.module.combat.autorer.modules

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 14:05 of 08.08.2022
 */
@ModuleInfo(
    name = "Crystals",
    desc = "Changes place position of AutoRer. Can be used as cev breaker",
    submodule = true
)
class Crystals : Module() {
    @JvmField val priority = register(Setting("Priority", this, true))!!
    private val leftClick = register(Setting("Left Click", this, false))

    var pos : BlockPos? = null
    var state
        get() = isToggled && pos != null
        set(value0) {
            if(!value0) {
                pos = null
            }
        }

    companion object {
        @JvmField var instance : Crystals? = null
    }

    init {
        instance = this
    }

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
    }

    @SubscribeEvent
    fun onLeftClickBlock(
        event : PlayerInteractEvent.LeftClickBlock
    ) {
        if(leftClick.valBoolean) {
            val block = block(event.pos)

            if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
                pos = event.pos
            }
        }
    }
}