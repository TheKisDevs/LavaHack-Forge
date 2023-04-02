package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.math.Animation
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.cubicgl.CubicGL
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * @author _kisman_
 * @since 12:28 of 30.06.2022
 */
class GLScissorTest : Module(
    "GLScissorTest",
    "Test of GL11.glScissor() for gui animations.",
    Category.DEBUG
) {
    private val opened = register(Setting("Opened", this, true))
    private val speed = register(Setting("Speed", this, 0.05, 0.01, 0.2, false))

    private val y = 10.0
    private val width = 100.0
    private val height = 500.0

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        Render2DUtil.drawRectWH(10.0, y, 100.0, 15.0, -1)

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)

        val sr = ScaledResolution(mc)
        val factor = sr.scaleFactor
        val translateY = sr.scaledHeight - (y + 10.0) - height

        val height = if(opened.valBoolean) {
            Animation.animate(this.height, height, speed.valDouble)
        } else {
            Animation.animate(0.0, height, speed.valDouble)
        }

//        GL11.glScissor(10 * factor, /*translateY.toInt()*/(10 + y).toInt() * factor, width.toInt() * factor, height.toInt() * factor)

        CubicGL.scissors(10, 10, width.toInt(), height.toInt())

        Render2DUtil.drawRectWH(1.0, 1.0, 600.0, 600.0, Color.RED.rgb)

        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()
    }
}