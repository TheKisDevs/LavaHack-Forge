package com.kisman.cc.features.module.render.charms.logoutspots

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.EntityCopied
import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

/**
 * @author _kisman_
 * @since 8:26 of 02.04.2023
 */
class EntityLogged(
    world : World,
    profile : GameProfile,
    player : EntityPlayer,
    private val id : Int
) : EntityCopied(
    world,
    profile,
    player
) {
    init {
        MinecraftForge.EVENT_BUS.register(this)

        sync()

        mc.addScheduledTask {
            mc.world.addEntityToWorld(id, this)
        }
    }

    @SubscribeEvent
    fun onDisconnect(
        event : FMLNetworkEvent.ClientDisconnectionFromServerEvent
    ) {
        mc.addScheduledTask {
            mc.world.removeEntityFromWorld(id)
        }
    }

    override fun getName() = "${player.name} just logged out at ${player.position.x} ${player.position.y} ${player.position.z}"
    override fun showNameTag() = true
}