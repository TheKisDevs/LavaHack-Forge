package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.chat.cubic.ChatUtility
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
@WorkInProgress
class PearlTracer : Module(
        "PearlTracer",
        "Sure?",
        Category.RENDER
) {
    private val test1 = register(Setting("Test 1", this, false))
    private val test2 = register(Setting("Test 2", this, false))
    private val depth = register(Setting("Depth", this, false))
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
            if(entity is EntityEnderPearl) {
                println("adding ender pearl")
                if(map.containsKey(entity)) {
                    map[entity]?.add(VectorWithTimer(entity.positionVector))
                } else {
                    map[entity] = ArrayList(listOf(VectorWithTimer(entity.positionVector)))
                }
            }
        }

        val pearlsToRemove = HashSet<EntityEnderPearl>()

        for(key in map.keys) {
            if(map[key]!!.isEmpty()) {
                pearlsToRemove.add(key)
            }

            val vectorsToRemove = HashSet<VectorWithTimer>();

            for(vec in map[key]!!) {
                if(vec.timer.passedMillis(removeTime.valLong)) {
                    println("removing vector")
                    vectorsToRemove.add(vec)
                }
            }

            map[key]!!.removeAll(vectorsToRemove)
        }

        for(pearl in pearlsToRemove) {
            map.remove(pearl)
        }

        var renderPosX = mc.renderManager.viewerPosX//renderPosX
        var renderPosY = mc.renderManager.viewerPosY//renderPosY
        var renderPosZ = mc.renderManager.viewerPosZ//renderPosZ

        if(test2.valBoolean) {
            renderPosX = mc.renderManager.renderPosX
            renderPosY = mc.renderManager.renderPosY
            renderPosZ = mc.renderManager.renderPosZ
        }

        glPushMatrix()
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_BLEND)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)
        glDepthMask(depth.valBoolean)
        if(test1.valBoolean) {
            mc.entityRenderer.disableLightmap()
        }
        glLineWidth(lineWidth.valFloat)
        resetColor()
        glBegin(GL_LINES)

        for(key in map.keys) {
            glBegin(GL_LINES)

            for(index in 1..map[key]!!.size) {
                val vec = map[key]!![index]
                val vec2 = map[key]!![index - 1]

                ChatUtility.info().printClientModuleMessage("${vec.vector.x} ${vec.vector.y} ${vec.vector.z}")
                getColour(index).glColor()
                glVertex3d(vec.vector.x - renderPosX, vec.vector.y - renderPosY, vec.vector.z - renderPosZ)
                glVertex3d(vec2.vector.x - renderPosX, vec2.vector.y - renderPosY, vec2.vector.z - renderPosZ)
            }

            glEnd()
        }

        glDisable(GL_BLEND)
        glEnable(GL_TEXTURE_2D)
        glDepthMask(!depth.valBoolean)
        glDisable(GL_LINE_SMOOTH)
        glPopMatrix()
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