package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.mixin.mixins.accessor.AccessorDestroyBlockProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.block
import com.kisman.cc.util.enums.AABBProgressModifiers
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
class BreakHighlight : ShaderableModule() {
    private val renderer = RenderingRewritePattern(this).preInit().init()
    private val logic = register(SettingEnum<AABBProgressModifiers>("Logic", this, AABBProgressModifiers.CentredBox))
    private val reverse = register(Setting("Reverse", this, false))
    private val easing = register(SettingEnum<EasingEnum.Easing>("Easing", this, EasingEnum.Easing.Linear))
    private val range = register(register(SettingGroup(Setting("Range", this))).add(SettingsList("state", Setting("Range Check", this, false).setTitle("State"), "value", Setting("Range", this, 50.0, 0.0, 100.0, true))))
    private val text = register(register(SettingGroup(Setting("Text", this))).add(SettingsList("percent", Setting("Text Percent", this, false).setTitle("Percent"), "name", Setting("Text Name", this, false).setTitle("Name"), "color", Setting("Text Color", this, Colour(255, 255, 255, 255)).setTitle("Color"))))

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        handleDraw(renderer)
    }

    override fun draw() {
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

            if(block(pos) != Blocks.AIR && (!range["state"].valBoolean || mc.player.getDistance(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) <= range["value"].valInt)) {
                val damage = progress.partialBlockDamage.coerceIn(0..8)
                val aabb = modify(pos, percent(damage, reverse.valBoolean))

                renderer.draw(aabb)

                val flag1 = text["percent"].valBoolean
                val flag2 = text["name"].valBoolean

                val string = "${if(flag1) {
                    "${percent(damage, false) * 100}%"
                } else {
                    ""
                }}${if(flag2) {
                    "${if(flag1) {
                        "\n"
                    } else {
                        ""
                    }}${mc.world.getEntityByID((progress as AccessorDestroyBlockProgress).entityID())?.name ?: "NULL"}"
                } else {
                    ""
                }}"


                if(flag1 || flag2) {
                    Rendering.TextRendering.drawText(
                        pos,
                        string,
                        text["color"].colour.rgb
                    )
                }
            }
        }
    }
}