package com.kisman.cc.features.module.misc

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils

class WebhookSpammer : Module("WebhookSpammer", "Using discord webhook for spamming", Category.MISC) {
    private val color = Setting("Color", this, "Embed Color", Colour(255, 0, 0))
    private val delay = Setting("Delay", this, 1000.0, 0.0, 10000.0, true)
    private val debug = Setting("Debug", this, false)

    private val timer = TimerUtils()

    init {
        setmgr.rSetting(color)
        setmgr.rSetting(delay)
        setmgr.rSetting(debug)
    }

    override fun onEnable() {
        timer.reset()
    }

    override fun isBeta() : Boolean {return true}

    override fun update() {
        if(mc.player == null || mc.world == null || !timer.passedMillis(delay.valLong)) return
        timer.reset()
//        DiscordWebhookSender.send(DDOSModule.customIp, color.colour, "Owned by " + mc.player.name + " with " + Kisman.getName() + " " + Kisman.getVersion())
    }
}