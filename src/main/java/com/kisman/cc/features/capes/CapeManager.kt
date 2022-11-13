package com.kisman.cc.features.capes

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage
import com.kisman.cc.sockets.setupSocketClient
import net.minecraft.entity.player.EntityPlayer

/**
 * @author _kisman_
 * @since 15:02 of 01.11.2022
 */
object CapeManager {
    private val capes = HashMap<String, Capes>()

    fun clear() {
        capes.clear()
    }

    fun add(
        uuid : String,
        cape : Capes
    ) {
        capes[uuid] = cape
    }

    fun add(
        uuid : String,
        name : String
    ) {
        try {
            add(uuid, Capes.valueOf(name))
        } catch(_ : Throwable) { }
    }

    fun remove(
        uuid : String
    ) {
        if(has(uuid)) {
            capes.remove(uuid)
        }
    }

    fun remove(
        player : EntityPlayer
    ) {
        remove(player.uniqueID.toString())
    }

    fun get(
        player : EntityPlayer
    ) : Capes? = get(player.uniqueID.toString())

    fun get(
        uuid : String
    ) : Capes? = capes[uuid]

    fun has(
        uuid : String
    ) : Boolean = capes.containsKey(uuid)

    fun has(
        player : EntityPlayer
    ) : Boolean = has(player.uniqueID.toString())

    fun getCape(
        name0 : String
    ) : Capes = try {
        Capes.valueOf(name0)
    } catch(_ : Throwable) {
        Capes.Release
    }
}