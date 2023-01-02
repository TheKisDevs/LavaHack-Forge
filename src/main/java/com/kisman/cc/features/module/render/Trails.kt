package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.render.Rendering
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.DimensionType
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

/**
 * @author _kisman_
 * @since 18:25 of 06.06.2022
 */
class Trails : Module(
    "Trails",
    "nice",
    Category.RENDER
) {
    private val color = register(Setting("Color", this, Colour(255, 255, 255, 255)))
    private val delay = register(Setting("Delay", this, 10.0, 1.0, 1000.0, NumberType.TIME))
    private val fade = register(Setting("Fade", this, true))
    private val fadeDelay = register(Setting("Fade Delay", this, 500.0, 10.0, 10000.0, NumberType.TIME).setVisible { fade.valBoolean })
    private val lineWidth = register(Setting("Line Width", this, 1.0, 0.1, 5.0, false))

    private val timer = TimerUtils()

    private val positions = ArrayList<Trace>()
    private var trace : Trace? = null

    companion object {
        val origin = Vec3d(8.0, 64.0, 8.0)
    }

    override fun onEnable() {
        super.onEnable()
        timer.reset()
        positions.clear()
        trace = null
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        if(timer.passedMillis(delay.valLong)) {
            val vec = mc.player.positionVector

            if(trace == null) {
                trace = Trace(
                    0,
                    null,
                    mc.world.provider.dimensionType,
                    vec,
                    ArrayList()
                )
            }

            val trace1 = trace!!.trace
            val vec3d = if(trace1.isEmpty()) vec else trace1[trace1.size - 1].pos

            if(trace1.isNotEmpty()) {
                positions.add(trace!!)
                trace = Trace(
                    positions.size + 1,
                    null,
                    mc.world.provider.dimensionType,
                    vec,
                    trace1
                )
            }

            if(trace1.isEmpty() || !vec.equals(vec3d)) {
                trace1.add(TracePos(
                    vec,
                    System.currentTimeMillis() + fadeDelay.valInt
                ))
            }

            timer.reset()
        }

        if(trace != null) {
            Rendering.start()
            GL11.glLineWidth(lineWidth.valFloat)

            for(pos in positions) {
                GL11.glBegin(GL11.GL_LINE_STRIP)
                for(tr in pos.trace) {
                    render(tr)
                }
                GL11.glEnd()

                if(fade.valBoolean) {
                    pos.trace.removeIf {
                        it.shouldBeRemove()
                    }
                }
            }

            GL11.glBegin(GL11.GL_LINE_STRIP)

            for(tr in trace?.trace!!) {
                render(tr)
            }

            if(fade.valBoolean) {
                trace?.trace!!.removeIf {
                    it.shouldBeRemove()
                }
            }

            GL11.glEnd()

            Rendering.end()
        }
    }

    private fun render(trace : TracePos) {
        val alpha = if(fade.valBoolean) {
            MathHelper.clamp((color.colour.a1 / fadeDelay.valInt) * (trace.time - System.currentTimeMillis()), 0f, color.colour.b1) / color.colour.b1
        } else {
            color.colour.a1
        }
        GL11.glColor4f(
            color.colour.r1,
            color.colour.g1,
            color.colour.b1,
            alpha
        )

        GL11.glVertex3d(
            trace.pos.x - mc.renderManager.renderPosX,
            trace.pos.y - mc.renderManager.renderPosY,
            trace.pos.z - mc.renderManager.renderPosZ
        )
    }

    class Trace(
        var index: Int,
        var name: String?,
        var type: DimensionType,
        var pos: Vec3d,
        val trace: ArrayList<TracePos>
    )

    class TracePos(
        var pos : Vec3d,
        var time : Long
    ) {
        val timer = TimerUtils()

        fun shouldBeRemove() : Boolean {
            return timer.passedMillis(2000L)
        }
    }
}