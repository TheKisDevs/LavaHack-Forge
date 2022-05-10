package com.kisman.cc.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.item.ItemPickaxe

class BlockInteraction : Module(
        "BlockInteraction",
        "NoMiningTrace + MultiTask",
        Category.PLAYER
) {
    private val noMiningTrace : Setting = register(Setting("No Mining Trace", this, false))
    private val nmtPickaxeOnly : Setting = register(Setting("NMT Pickaxe Only", this, false).setVisible { noMiningTrace.valBoolean })
    val multiTask : Setting = register(Setting("Multi Task", this, false))

    override fun onEnable() {
        Kisman.EVENT_BUS.subscribe(renderGetEntitiesINAABBexcluding)
    }

    override fun onDisable() {
        Kisman.EVENT_BUS.unsubscribe(renderGetEntitiesINAABBexcluding)
    }

    private val renderGetEntitiesINAABBexcluding = Listener<EventRenderGetEntitiesINAABBexcluding>(EventHook {
        if(noMiningTrace.valBoolean && (!nmtPickaxeOnly.valBoolean || mc.player.heldItemMainhand.item is ItemPickaxe)) it.cancel()
    })
}