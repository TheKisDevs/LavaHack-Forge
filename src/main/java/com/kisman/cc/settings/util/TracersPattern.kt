package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Module
import com.kisman.cc.mixin.mixins.accessor.AccessorBufferBuilder
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.TracersSetting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.enums.TracersEntityTypes
import com.kisman.cc.util.enums.TracersModes
import com.kisman.cc.util.enums.TracersSettingTypes
import com.kisman.cc.util.math.interpolate
import com.kisman.cc.util.math.toRadians
import com.kisman.cc.util.render.Rendering.release
import com.kisman.cc.util.render.Rendering.setup
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.*

/**
 * @author _kisman_
 * @since 16:55 of 04.03.2023
 */
class TracersPattern(
    module : Module
) : AbstractPattern<TracersPattern>(
    module
) {
    private val width = Setting("Width", module, 1.0, 0.1, 5.0, false)

    private val groups = mutableListOf<SettingGroup>()
    private val settings = mutableListOf<TracersSetting>()

    private val tessellator = Tessellator(2097152)

    private var flag = false

    init {
        for(type in TracersEntityTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))

            groups.add(group)

            settings.add(TracersSetting(group.add(Setting("${type.name} Mode", module, TracersModes.None).setTitle("Mode")), type, TracersSettingTypes.Mode))
            settings.add(TracersSetting(group.add(Setting("${type.name} Color 1", module, Colour(-1)).setTitle("First")), type, TracersSettingTypes.Color1))
            settings.add(TracersSetting(group.add(Setting("${type.name} Color 2", module, Colour(-1)).setTitle("Second")), type, TracersSettingTypes.Color2))
//            settings.add(TracersSetting(group.add(Setting("${type.name} Width", module, 1.0, 0.1, 5.0, false).setTitle("Width")), type, TracersSettingTypes.Width))
        }
    }

    override fun preInit() : TracersPattern {
        if(this.group != null) {
            for(group in groups) {
                this.group!!.add(width)
                this.group!!.add(group)
            }
        }

        return this
    }

    override fun init() : TracersPattern {
        module.register(width)

        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            module.register(setting.setting)
        }

        return this
    }

    private fun getSetting(
        typeE : TracersEntityTypes,
        typeS : TracersSettingTypes
    ) : Setting? {
        for(setting in settings) {
            if(setting.typeE == typeE && setting.typeS == typeS) {
                return setting.setting
            }
        }

        return null
    }

    fun preUpdateEntities() {
        try {
            if(Kisman.callingFromGameLoop) {
                tessellator.buffer.begin(GL_LINES, POSITION_COLOR)
                flag = false
            }
        } catch(_ : Exception) { }
    }

    fun updateEntity(
        entity : Entity
    ) {
        if(entity != mc.player && mc.player != null && !flag && (tessellator.buffer as AccessorBufferBuilder).drawing() && Kisman.callingFromGameLoop) {
            val type = TracersEntityTypes.get(entity)

            if (type != null) {
                val mode = getSetting(type, TracersSettingTypes.Mode)!!

                if (mode.valEnum != TracersModes.None) {
                    val eyes = Vec3d(0.0, 0.0, 1.0).rotatePitch(-toRadians(mc.player.rotationPitch)).rotateYaw(toRadians(-mc.player.rotationYaw))

                    val interpolation = interpolate(entity, mc.renderPartialTicks)

                    val x = interpolation.x - mc.renderManager.viewerPosX
                    val y = interpolation.y + (entity.boundingBox.maxY - entity.boundingBox.minY) / 2 - mc.renderManager.viewerPosY
                    val z = interpolation.z - mc.renderManager.viewerPosZ

                    val color1 = getSetting(type, TracersSettingTypes.Color1)!!.colour
                    val color2 = if(mode.valEnum == TracersModes.Normal) color1 else getSetting(type, TracersSettingTypes.Color2)!!.colour

//                    fun vertexes() {

//                    }

                    /*try {
                        vertexes()
                    } catch(_ : IndexOutOfBoundsException) {
                        tessellator.buffer.endVertex()
                        (tessellator.buffer as AccessorBufferBuilder).vertexCount(tessellator.buffer.vertexCount - 1)

                        vertexes()
                    }*/
                    tessellator.buffer.pos(eyes.x, eyes.y + mc.player.eyeHeight, eyes.z).color(color1.r, color1.g, color1.b, color1.a).endVertex()
                    tessellator.buffer.pos(x, y, z).color(color2.r, color2.g, color2.b, color2.a).endVertex()
//                    vertexes()
                }
            }
        }
    }

    fun postUpdateEntities() {
        /*if(mc.player == null) {
            tessellator.buffer.reset()
        }*/
    }

    fun postRenderEntities() {
        if((tessellator.buffer as AccessorBufferBuilder).drawing()) {
            val bobbing = mc.gameSettings.viewBobbing

            mc.gameSettings.viewBobbing = false
            mc.entityRenderer.setupCameraTransform(mc.renderPartialTicks, 0)

            setup()

            glLineWidth(width.valFloat)

            tessellator.draw()

            release()

            mc.gameSettings.viewBobbing = bobbing
            mc.entityRenderer.setupCameraTransform(mc.renderPartialTicks, 0)
        }
    }
}