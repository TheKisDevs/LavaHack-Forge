package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.EntityESPSetting
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.enums.EntityESPModes
import com.kisman.cc.util.enums.EntityESPTypes
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import java.util.function.Supplier
import kotlin.collections.ArrayList

class EntityESPRendererPattern(
        val module : Module
) {
    private val groups = ArrayList<SettingGroup>()
    private val settings = ArrayList<EntityESPSetting>()

    private val threads = MultiThreaddableModulePattern(module)

    private var entities = ArrayList<Entity>()

    init {
        for(type in EntityESPTypes.values()) {
            val group = SettingGroup(Setting(type.name, module))
            val patternGroup = group.add(SettingGroup(Setting("Cubic Renderer", module)))
            groups.add(group)
            groups.add(patternGroup)
            settings.addAll(getSettingsByType(type, group, patternGroup))
        }
    }

    fun init() {
        for(group in groups) {
            module.register(group)
        }

        for(setting in settings) {
            if(setting.setting != null) {
                module.register(setting.setting)
            } else if(setting.pattern != null) {
                setting.pattern.init()
            }
        }
    }

    private fun getSettingsByType(typeE : EntityESPTypes, group : SettingGroup, patternGroup : SettingGroup) : ArrayList<EntityESPSetting> {
        val mode = Setting("${typeE.name} Mode", module, EntityESPModes.None)
        val pattern = RenderingRewritePattern(module).group(patternGroup).visible(Supplier{ mode.valEnum == EntityESPModes.Cubic }).prefix("${typeE.name} Cubic").preInit()
        return ArrayList(listOf(
            EntityESPSetting(group.add(mode), null, typeE, SettingTypes.Mode),
            EntityESPSetting(group.add(Setting("${typeE.name} Box1 Color", module, "${typeE.name} Box1 Color", Colour(255, 255, 255, 255)).setVisible { mode.valEnum == EntityESPModes.Box1 }), null, typeE, SettingTypes.Box1Color),
            EntityESPSetting(null, pattern, typeE, SettingTypes.CubicPattern)
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
                if(mode?.setting != null && mode.setting.valEnum != EntityESPModes.None) list.add(entity)
            }

            mc.addScheduledTask { entities = list }
        })

        for(entity in entities) {
            drawEntity(ticks, entity)
        }
    }

    private fun drawEntity(ticks : Float, entity : Entity) {
        val setting = getSettingByType(SettingTypes.Mode, entity)!!.setting!!
        if(setting.valEnum != EntityESPModes.None) {
            when(setting.valEnum) {
                EntityESPModes.None -> entity.glowing = false
                EntityESPModes.Glow -> entity.glowing = true
                EntityESPModes.Box1 -> {
                    entity.glowing = false
                    val color = getSettingByType(SettingTypes.Box1Color, entity)!!.setting!!.colour
                    Rendering.drawBoxESP(entity, color.r1, color.g1, color.b1, 1f, ticks)
                }
                EntityESPModes.Cubic -> {
                    entity.glowing = false
                    getSettingByType(SettingTypes.CubicPattern, entity)!!.pattern!!.draw(entity.entityBoundingBox)
                }
            }
        }
    }

    private fun getSettingByType(typeS : SettingTypes, typeE : EntityESPTypes?) : EntityESPSetting? {
        for(setting in settings) {
            if(setting.typeE == typeE && setting.typeS == typeS) {
                return setting
            }
        }
        return null
    }

    private fun getSettingByType(
        typeS : SettingTypes,
        entity : Entity
    ) : EntityESPSetting? = getSettingByType(typeS, EntityESPTypes.get(entity))

    enum class SettingTypes {
        Mode,
        Box1Color,
        CubicPattern
    }
}