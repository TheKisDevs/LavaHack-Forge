package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.SettingPair
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.block
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.state
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 16:01 of 12.03.2023
 */
@Suppress("unused", "UNUSED_PARAMETER")
@ModuleInfo(
    name = "BreakHighlight",
    desc = "Highlights breaking blocks",
    category = Category.RENDER
)
class BreakHighlight : Module() {
    private val renderer = RenderingRewritePattern(this).preInit().init()
    private val logic = register(SettingEnum<Logic>("Logic", this, Logic.CentredBox))
    private val reverse = register(Setting("Reverse", this, false))
    private val easing = register(SettingEnum<EasingEnum.Easing>("Easing", this, EasingEnum.Easing.Linear))
    private val range = register(register(SettingGroup(Setting("Range", this))).add(SettingPair<Setting, Setting>(Setting("Range Check", this, false).setTitle("State"), Setting("Range", this, 50.0, 0.0, 100.0, true))))
    private val text = register(register(SettingGroup(Setting("Text", this))).add(SettingPair<Setting, Setting>(Setting("Text State", this, false).setTitle("State"), Setting("Text Color", this, Colour(255, 255, 255, 255)).setTitle("Color"))))

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        fun modify(
            pos : BlockPos,
            percent : Double
        ) : AxisAlignedBB = logic.valEnum.modifier.modify(state(pos).getSelectedBoundingBox(mc.world, pos), easing.valEnum.task.doTask(percent))

        fun percent(
            damage : Int,
            reverse : Boolean
        ) : Double {
            var percent = (damage / 8.0).coerceIn(0.0..1.0)

            if(reverse) {
                percent = 1 - percent
            }

            return percent
        }

        for(entry in mc.renderGlobal.damagedBlocks) {
            val progress = entry.value
            val pos = progress.position

            if(block(pos) != Blocks.AIR && (!range.first.valBoolean || mc.player.getDistance(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) <= range.second.valInt)) {
                val damage = progress.partialBlockDamage.coerceIn(0..8)
                val aabb = modify(pos, percent(damage, reverse.valBoolean))

                renderer.draw(aabb)

                if(text.first.valBoolean) {
                    Rendering.TextRendering.drawText(
                        pos,
                        "${percent(damage, false) * 100}%",
                        text.second.colour.rgb
                    )
                }
            }
        }
    }

    enum class Logic(
        val modifier : IModifier
    ) {
        CentredBox(object : IModifier {
            override fun modify(
                aabb : AxisAlignedBB,
                percent : Double
            ) : AxisAlignedBB {
                fun offset(
                    aabb : AxisAlignedBB
                ) : AxisAlignedBB = AxisAlignedBB(
                    aabb.minX + 0.5,
                    aabb.minY + 0.5,
                    aabb.minZ + 0.5,
                    aabb.maxX - 0.5,
                    aabb.maxY - 0.5,
                    aabb.maxZ - 0.5
                )

                return offset(Rendering.scale(aabb, percent))
            }
        }),

        BottomColumn(object : IModifier {
            override fun modify(
                aabb : AxisAlignedBB,
                percent : Double
            ) : AxisAlignedBB = AxisAlignedBB(
                aabb.minX,
                aabb.minY,
                aabb.minZ,
                aabb.maxX,
                aabb.maxY - (aabb.maxY - aabb.minY) * percent,
                aabb.maxZ
            )
        }),

        TopColumn(object : IModifier {
            override fun modify(
                aabb : AxisAlignedBB,
                percent : Double
            ) : AxisAlignedBB = AxisAlignedBB(
                aabb.minX,
                aabb.minY + (aabb.maxY - aabb.minY) * (1 - percent),
                aabb.minZ,
                aabb.maxX,
                aabb.maxY,
                aabb.maxZ
            )
        })
    }

    interface IModifier {
        fun modify(
            aabb : AxisAlignedBB,
            percent : Double
        ) : AxisAlignedBB
    }
}