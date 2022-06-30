package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.EntitySetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.RenderUtil
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.enums.EntityESPModes
import com.kisman.cc.util.enums.EntityESPTypes
import com.kisman.cc.util.enums.RenderingRewriteModes
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import java.util.function.Supplier
import kotlin.collections.ArrayList

class EntityESPRendererPattern(
        val module : Module
) {
    val groups = ArrayList<SettingGroup>()
    val settings = ArrayList<EntitySetting>()

    private val threads = MultiThreaddableModulePattern(module)

    private var entities = ArrayList<Entity>()

    init {
        for(type in EntityESPTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))
            groups.add(group)
            settings.addAll(getSettingsByType(type, group))
        }
    }

    fun init() {
        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            module.register(setting.setting)
        }
    }

    private fun getSettingsByType(typeE : EntityESPTypes, group : SettingGroup) : ArrayList<EntitySetting> {
        val mode = Setting("${typeE.name} Mode", module, EntityESPModes.None)
        val pattern = RenderingRewritePattern(module, Supplier{ mode.valEnum == EntityESPModes.Cubic }, "${typeE.name} Cubic")
        return ArrayList(listOf(
                EntitySetting(group.add(mode), typeE, SettingTypes.Mode),
                EntitySetting(group.add(Setting("${typeE.name} Box1 Color", module, "${typeE.name} Box1 Color", Colour(255, 255, 255, 255)).setVisible { mode.valEnum == EntityESPModes.Box1 }), typeE, SettingTypes.Box1Color),
                EntitySetting(group.add(pattern.mode), typeE, SettingTypes.CubicMode),
                EntitySetting(group.add(pattern.lineWidth), typeE, SettingTypes.CubicLineWidth),
                EntitySetting(group.add(pattern.color1), typeE, SettingTypes.CubicColor1),
                EntitySetting(group.add(pattern.color2), typeE, SettingTypes.CubicColor2)
        ))
    }

    fun onEnable() {
        threads.reset()
    }

    fun draw(ticks : Float) {
        val mc = Minecraft.getMinecraft()

        threads.update(Runnable {
            val list = ArrayList<Entity>()

            for(entity in mc.world.loadedEntityList) {
                if(entity == mc.player) continue
                val mode = getSettingByType(SettingTypes.Mode, EntityESPTypes.get(entity))
                if(mode != null && mode.valEnum != EntityESPModes.None) list.add(entity)
            }

            mc.addScheduledTask { entities = list }
        })

        for(entity in entities) {
            drawEntity(ticks, entity)
        }
    }

    private fun drawEntity(ticks : Float, entity : Entity) {
        val setting = getSettingByType(SettingTypes.Mode, EntityESPTypes.get(entity))
        if(setting!!.valEnum != EntityESPModes.None) {
            when(setting.valEnum) {
                EntityESPModes.None -> entity.glowing = false
                EntityESPModes.Glow -> entity.glowing = true
                EntityESPModes.Box1 -> {
                    entity.glowing = false
                    val color = getSettingByType(SettingTypes.Box1Color, EntityESPTypes.get(entity))!!.colour
                    RenderUtil.drawESP(entity, color.r1, color.g1, color.b1, 1f, ticks)
                }
                EntityESPModes.Cubic -> {
                    entity.glowing = false
                    Rendering.draw(
                            Rendering.correct(entity.entityBoundingBox),
                            getSettingByType(SettingTypes.CubicLineWidth, EntityESPTypes.get(entity))!!.valFloat,
                            getSettingByType(SettingTypes.CubicColor1, EntityESPTypes.get(entity))!!.colour,
                            getSettingByType(SettingTypes.CubicColor2, EntityESPTypes.get(entity))!!.colour,
                            (getSettingByType(SettingTypes.CubicMode, EntityESPTypes.get(entity))!!.valEnum as RenderingRewriteModes).mode
                    )
                }
            }
        }
    }

    private fun getSettingByType(typeS : SettingTypes, typeE : EntityESPTypes?) : Setting? {
        for(setting in settings) {
            if(setting.typeE == typeE && setting.typeS == typeS) {
                return setting.setting
            }
        }
        return null
    }

    enum class SettingTypes {
        Mode, CubicMode, Box1Color, CubicLineWidth, CubicColor1, CubicColor2
    }
}