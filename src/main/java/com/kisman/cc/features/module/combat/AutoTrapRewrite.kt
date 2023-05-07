package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.subsystem.subsystems.Targetable
import com.kisman.cc.features.subsystem.subsystems.nearest
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.ObbyPlacementPattern
import com.kisman.cc.util.world.canPlace
import com.kisman.cc.util.world.dynamicBlocks
import com.kisman.cc.util.world.feetBlocks
import com.kisman.cc.util.world.helpingBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author _kisman_
 * @since 12:35 of 05.05.2023
 */
@ModuleInfo(
    name = "AutoTrapRewrite",
    display = "AutoTrap",
    category = Category.COMBAT,
    targetable = Targetable(
        nearest = true
    )
)
class AutoTrapRewrite : Module() {
    private val range = register(Setting("Range", this, 5.0, 3.0, 6.0, false))
    private val feet = register(Setting("Feet", this, true))
    private val avoid = register(SettingsList("step", Setting("Avoid Step", this, false), "scaffold", Setting("Avoid Scaffold", this, false)))
    private val bpt = register(Setting("Bpt", this, 4.0, 0.0, 20.0, true).setTitle("B/T"))
    private val delay = register(Setting("Delay", this, 0.0, 0.0, 1000.0, NumberType.TIME))

    private val placer = ObbyPlacementPattern(this, true).preInit().init()

    private val queue = ConcurrentLinkedQueue<BlockPos>()

    private val timer = timer()

    private var target : EntityPlayer? = null

    init {
        setDisplayInfo { "[${target?.name ?: "no target no fun"}]" }
        instance = this
    }

    companion object {
        @JvmField var instance : AutoTrapRewrite? = null
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        target = nearest()

        if(target != null) {
            trap(target!!)
        }
    }

    private fun trap(
        player : EntityPlayer
    ) {
        if(timer.passedMillis(delay.valLong)) {
            val dynamic = dynamicBlocks(player)
            val base = feetBlocks(player)

            fun processCollection(
                posses : Collection<BlockPos>,
                validator : (BlockPos) -> Boolean,
                modifier : (BlockPos) -> BlockPos
            ) {
                for (pos in posses) {
                    val modified = modifier(pos)

                    if (validator(modified)) {
                        val distance = mc.player.getDistanceSq(modified)

                        if (distance <= range.valDouble * range.valDouble) {
                            queue.add(modified)

                            if(!canPlace(modified)) {
                                val helping = helpingBlock(modified) { return@helpingBlock !base.contains(it) && !base.contains(it.down()) }

                                if(helping != null) {
                                    queue.add(helping)
                                }
                            }
                        }
                    }
                }
            }

            var flag1 = true
            var flag2 = true

            processCollection(dynamic,
                {
                    if (!feet.valBoolean) {
                        if (flag1) {
                            flag1 = false

                            return@processCollection true
                        }
                    }

                    return@processCollection false
                },
                { return@processCollection it }
            )

            processCollection(dynamic,
                { return@processCollection true },
                { return@processCollection it.up() }
            )

            processCollection(dynamic,
                {
                    if (!avoid["step"].valBoolean) {
                        if (flag2) {
                            flag2 = false

                            return@processCollection true
                        }
                    }

                    return@processCollection false
                },
                { return@processCollection it.up(2) }
            )

            processCollection(base,
                { return@processCollection true },
                { return@processCollection it.up(2) }
            )

            processCollection(base,
                { return@processCollection avoid["scaffold"].valBoolean },
                { return@processCollection it.up(3) }
            )


            for (n in 1 until bpt.valInt) {
                val pos = queue.poll()

                if (pos != null) {
                    placer.placeBlockSwitch(pos, Blocks.OBSIDIAN)
                } else {
                    break
                }
            }

            timer.reset()
        }
    }
}