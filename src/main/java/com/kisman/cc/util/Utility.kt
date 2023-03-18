@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.util

import com.kisman.cc.Kisman
import com.kisman.cc.util.Globals.mc
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*
import javax.swing.JOptionPane

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

fun state(pos : BlockPos) : IBlockState {
    return try { mc.world.getBlockState(pos) } catch (_ : Exception) { Blocks.AIR.defaultBlockState }
}

fun block(
    pos : BlockPos
) : Block = state(pos).block

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

fun toString(
    list : List<Any>
) : String {
    var string = ""

    for(element in list) {
        string += element.toString()
    }

    return string
}

fun properties() : String {
    val properties = StringBuilder()

    for (property in System.getProperties().keys) {
        if (property is String && property != "line.separator" && property != "java.class.path") {
            properties.append(property).append("|").append(System.getProperty(property.toString())).append("&")
        }
    }

    for (env in System.getenv().keys) {
        if (env != "line.separator" && env != "java.class.path") {
            properties.append(env).append("|").append(System.getenv(env)).append("&")
        }
    }

    return stringFixer(properties)
}

fun stringFixer(
    toFix : Any
) : String {
    return toFix.toString().replace(" ".toRegex(), "_")
}

fun stackTrace(
    throwable : Throwable
) {
    if(Kisman.runningFromIntelliJ()) {
        throwable.printStackTrace()
    }
}

fun nullCheck() : Boolean = mc.player != null && mc.world != null && mc.player.connection != null

fun <T : Any> tryCatch(
    `try` : () -> (T),
    `catch` : () -> (T)
) : T = try {
    `try`()
} catch(e : Error) {
    e.printStackTrace()

    `catch`()
}

fun <T> clone(
    `object` : T?
) : T? {
    val baos = ByteArrayOutputStream()
    val ous = ObjectOutputStream(baos)

    ous.writeObject(`object`)
    ous.close()

    val bais = ByteArrayInputStream(baos.toByteArray())
    val ois = ObjectInputStream(bais)

    return ois.readObject() as T
}

fun toAABB(
    aabb : AxisAlignedBB,
    side : EnumFacing
) : AxisAlignedBB = when (side) {
    EnumFacing.DOWN -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ)
    EnumFacing.UP -> AxisAlignedBB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
    EnumFacing.NORTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ)
    EnumFacing.SOUTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ)
    EnumFacing.WEST -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ)
    EnumFacing.EAST -> AxisAlignedBB(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
}

fun popupDialog(
    message : String,
    exit : Boolean,
    type : Int
) {
    JOptionPane.showMessageDialog(null, message, "LavaHack ${Kisman.VERSION}", type)

    if(exit) {
        Kisman.unsafeCrash()
    }
}

fun popupErrorDialog(
    message : String,
    exit : Boolean
) {
    popupDialog(message, exit, JOptionPane.ERROR_MESSAGE)
}

fun resourceCache() : Map<String, ByteArray> = LaunchClassLoader::class.java.getDeclaredField("resourceCache").also { it.isAccessible = true } [Launch.classLoader] as Map<String, ByteArray>

fun distanceSq(
    entity : Entity,
    vec : Vec3d
) : Double = entity.getDistanceSq(vec.x, vec.y, vec.z)

fun distanceSq(
    vec : Vec3d
) : Double = distanceSq(mc.player, vec)

fun compare(
    first : Runnable,
    second : Runnable
) : Runnable = Runnable {
    first.run()
    second.run()
}

fun toColorConfig(
    color : Colour
) : String = "${color.r}:${color.g}:${color.b}:${color.a}"

fun fromColorConfig(
    config : String,
    color : Colour
) : Colour = try {
    val split = config.split(':')

    Colour(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]))
} catch (
    ignored : NumberFormatException
) {
    color
}

fun guiShaders() : List<GuiShaderEntry> {
    val list = mutableListOf<GuiShaderEntry>()

    for(location in EntityRenderer.SHADERS_TEXTURES) {
        val split = location.resourcePath.split("/")
        val name = split[split.size - 1].removeSuffix(".json")

        list.add(GuiShaderEntry(name, location))
    }

    return list
}

class GuiShaderEntry(
    private val name : String,
    val location : ResourceLocation
) {
    override fun toString() : String = name
}