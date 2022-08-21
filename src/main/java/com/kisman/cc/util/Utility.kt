package com.kisman.cc.util

import com.kisman.cc.util.Globals.mc
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
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

//TODO: PingBypass check
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

fun getBlockStateSafe(pos : BlockPos) : IBlockState {
    return try { mc.world.getBlockState(pos) } catch (ignored : Exception) { Blocks.AIR.defaultBlockState }
}

fun contains(
    ch : Char,
    array : CharArray
) : Boolean {
    for (c in array) {
        if (ch == c) {
            return true
        }
    }
    return false
}

fun toUrl(url : String) : URL? {
    return try {
        URL(url)
    } catch (e : MalformedURLException) {
        throw RuntimeException(e)
    }
}

fun toUrl(uri : URI) : URL? {
    return try {
        uri.toURL()
    } catch (e : MalformedURLException) {
        throw RuntimeException(e)
    }
}

fun sr() : ScaledResolution {
    return ScaledResolution(mc)
}