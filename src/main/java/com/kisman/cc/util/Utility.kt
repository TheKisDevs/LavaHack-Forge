package com.kisman.cc.util

import com.kisman.cc.util.Globals.mc
import net.minecraft.entity.player.EntityPlayer
import java.util.*

/**
 * @author _kisman_
 * @since 15:24 of 30.07.2022
 */
fun getPing(player : EntityPlayer) : Int {
    return getPing(player.uniqueID)
}

fun getPing() : Int {
    return getPing(mc.player.connection.gameProfile.id)
}

fun getPing(id : UUID) : Int {
    return if(mc.isSingleplayer) 0 else try { mc.player.connection.getPlayerInfo(id).responseTime } catch(ignored : Exception) { -1 }
}

//It's useless for java, but useful for kotlin - _kisman_
fun createDoubleArray(vararg elements : Double) : DoubleArray {
    val array = DoubleArray(elements.size)

    for(i in 0..elements.size) {
        array[i] = elements[i]
    }

    return array
}