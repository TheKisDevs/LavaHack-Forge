package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.enums.GradientModes
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.client.renderer.GlStateManager.resetColor
import net.minecraft.entity.item.EntityEnderPearl
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author _kisman_
 * @since 14:07 of 15.05.2022
 */
class PearlTracer : Module(
        "PearlTracer",
        "Sure?",
        Category.RENDER
) {
    private val lineWidth = register(Setting("Line Width", this, 1.0, 0.1, 5.0, false))
    private val removeTime = register(Setting("Remove Time", this, 1000.0, 100.0, 5000.0, NumberType.TIME))

    private val color = register(SettingGroup(Setting("Color", this)))
    private val gradient = register(color.add(Setting("Gradient", this, GradientModes.None)))
    private val gradientDiff = register(color.add(Setting("Diff", this, 1.0, 1.0, 1000.0, NumberType.TIME)))
    private val colorVal = register(color.add(Setting("Color", this, "Color", Colour(-1))))

    private val map = HashMap<EntityEnderPearl, ArrayList<VectorWithTimer>>()

    override fun onEnable() {
        super.onEnable()
        map.clear()
    }

    override fun update() {
        if(mc.world == null && map.isNotEmpty()) map.clear()
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        for(entity in mc.world.loadedEntityList) {
            if(entity != null && entity is EntityEnderPearl) {
                if(map.containsKey(entity)) {
                    map[entity]?.add(VectorWithTimer(entity.positionVector))
                } else {
                    map[entity] = ArrayList(listOf(VectorWithTimer(entity.positionVector)))
                }
            }
        }

        for(key in map.keys) {
            for(vec in map[key]!!) {// <-- Issue with crash btw
                if(vec.timer.passedMillis(removeTime.valLong)) {
                    map[key]!!.remove(vec)
                }
            }
        }

        val renderPosX = mc.renderManager.renderPosX
        val renderPosY = mc.renderManager.renderPosY
        val renderPosZ = mc.renderManager.renderPosZ

        for(key in map.keys) {
            glPushMatrix()
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_BLEND)
            glDisable(GL_DEPTH_TEST)
            glDisable(GL_CULL_FACE)
            mc.entityRenderer.disableLightmap()
            glLineWidth(lineWidth.valFloat)
            resetColor()
            glBegin(GL_LINE_STRIP)

            for((index, vec) in map[key]!!.withIndex()) {
                getColour(index).glColor()
                glVertex3d(vec.vector.x - renderPosX, vec.vector.y - renderPosY, vec.vector.z - renderPosZ)
            }

            glEnd()
            glDisable(GL_BLEND)
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_LINE_SMOOTH)
            glPopMatrix()
        }
    }

    private fun getColour(index : Int) : Colour {
        return when(gradient.valEnum as GradientModes) {
            GradientModes.None -> colorVal.colour
            GradientModes.Rainbow -> Colour(ColorUtils.injectAlpha(ColorUtils.rainbow(index * gradientDiff.valInt, colorVal.colour.saturation, colorVal.colour.brightness), colorVal.colour.alpha))
            GradientModes.Astolfo -> Colour(ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(index * gradientDiff.valInt), colorVal.colour.alpha))
            GradientModes.Pulsive -> Colour(ColorUtils.injectAlpha(ColorUtils.twoColorEffect(colorVal.colour, colorVal.colour.setBrightness(0.25f), (index * gradientDiff.valInt).toDouble()).color, colorVal.colour.alpha))
        }
    }

    class VectorWithTimer(
            val vector : Vec3d
    ) {
        val timer = TimerUtils()
    }
}