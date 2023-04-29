package com.kisman.cc.features.module.movement

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventPlayerMove
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.util.enums.SpeedModes
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

/**
 * @author _kisman_
 * @since 15:21 of 21.04.2023
 */
@ModuleInfo(
    name = "SpeedRewrite2",
    display = "Speed",
    category = Category.MOVEMENT
)
class SpeedRewrite2 : Module() {
    private val liquids = register(Setting("Liquids", this, false))
    private val mode = register(SettingEnum("Mode", this, SpeedModes.Strafe))

    init {
        setDisplayInfo { "[${mode.valEnum}]" }
        SpeedModes.init(this)
        instance = this
    }

    companion object {
        @JvmField var instance : SpeedRewrite2? = null
    }

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(move)

        for(mode in SpeedModes.values()) {
            mode.handler.enable()
        }
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(move)

        for(mode in SpeedModes.values()) {
            mode.handler.disable()
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null || (!liquids.valBoolean && (mc.player.isInWater || mc.player.isInLava))) {
            return
        }

        mode.valEnum.handler.update()
    }

    private val move = Listener<EventPlayerMove>(EventHook {
        if(!liquids.valBoolean && (mc.player.isInWater || mc.player.isInLava)) {
            return@EventHook
        }

        mode.valEnum.handler.move(it)
    })
}