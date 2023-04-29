@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.util

import com.kisman.cc.Kisman
import com.kisman.cc.gui.api.Draggable
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.EntityCopied
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.input.Mouse
import java.awt.Desktop
import java.awt.Point
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*
import javax.swing.JOptionPane
import kotlin.math.min

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
    if(fromIntellij()) {
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

fun fix(
    draggable : Draggable
) {
    val sr = ScaledResolution(mc)

    if(draggable.getX() < 0.0) {
        draggable.setX(0.0)
    }

    if(draggable.getY() < 0.0) {
        draggable.setY(0.0)
    }

    if(draggable.getX() > 0.0 && draggable.getX() + draggable.getW() > sr.scaledWidth) {
        draggable.setX(sr.scaledWidth - draggable.getW())
    }

    if(draggable.getY() > 0.0 && draggable.getY() + draggable.getH() > sr.scaledHeight) {
        draggable.setY(sr.scaledHeight - draggable.getH())
    }
}

val potions = listOf(
    MobEffects.SPEED,
    MobEffects.SLOWNESS,
    MobEffects.HASTE,
    MobEffects.MINING_FATIGUE,
    MobEffects.STRENGTH,
    MobEffects.INSTANT_HEALTH,
    MobEffects.INSTANT_DAMAGE,
    MobEffects.JUMP_BOOST,
    MobEffects.NAUSEA,
    MobEffects.REGENERATION,
    MobEffects.RESISTANCE,
    MobEffects.FIRE_RESISTANCE,
    MobEffects.WATER_BREATHING,
    MobEffects.INVISIBILITY,
    MobEffects.BLINDNESS,
    MobEffects.NIGHT_VISION,
    MobEffects.HUNGER,
    MobEffects.WEAKNESS,
    MobEffects.POISON,
    MobEffects.WITHER,
    MobEffects.HEALTH_BOOST,
    MobEffects.ABSORPTION,
    MobEffects.SATURATION,
    MobEffects.GLOWING,
    MobEffects.LEVITATION,
    MobEffects.LUCK,
    MobEffects.UNLUCK
)

fun findName(
    uuid : UUID
) : String? {
    for(info in mc.player.connection.playerInfoMap) {
        val profile = info.gameProfile

        if(profile.id == uuid) {
            return profile.name
        }
    }

    return null
}

/**
 * Fix of NoSuchMethodError: ClientRegistry.registerKeyBinding(KeyBinding)
 *
 * Only in loader environment
 *
 * Only with custom classloader
 */
fun registerKeyBinding(
    key : KeyBinding
) {
    mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, key)
}

fun copy(
    from : EntityPlayer,
    to : EntityCopied
) {
    to.copyLocationAndAnglesFrom(from)
    to.rotationYaw = from.rotationYaw
    to.rotationPitch = from.rotationPitch
    to.rotationYawHead = from.rotationYawHead
    to.renderYawOffset = from.renderYawOffset
    to.prevRotationYaw = from.prevRotationYaw
    to.prevRotationYawHead = from.prevRotationYawHead
    to.prevRenderYawOffset = from.prevRenderYawOffset
    to.setPositionAndRotationDirect(from.posX, from.posY, from.posZ, from.rotationYaw, from.rotationPitch, 0, false)
    to.health = 20f
    to.noClip = true
    to.onLivingUpdate()
}

fun openLink(
    link : String
) {
    try {
        val desktop = Desktop.getDesktop()

        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(link))
        }
    } catch(_ : Throwable) { }
}

fun fromIntellij() = System.getProperty("java.class.path")!!.contains("idea_rt.jar")

fun mouse() : Point {
    var scale = mc.gameSettings.guiScale
    var factor = 0

    if(scale == 0) {
        scale = 1000
    }

    while(factor < scale && mc.displayWidth / (factor + 1) >= 320 && mc.displayHeight / (factor + 1) >= 240) {
        factor++
    }

    return Point(Mouse.getX() / factor, mc.displayHeight / factor - Mouse.getY() / factor - 1)
}

fun merge(
    sequences : Array<CharSequence>,
    off : Int,
    len : Int
) : CharSequence {
    val builder = StringBuilder(sequences.size * 8)
    val max = min(sequences.size, len)

    for(i in off until max) {
        builder.append(sequences[i])

        if(i < max - 1) {
            builder.append(' ')
        }
    }

    return builder
}

fun string2int(
    text : String
) : Int {
    var result = -1

    for(char in text.chars()) {
        result -= char
    }

    return result
}

const val FLOAT_REGEX = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$"

fun parseNumber(
    text : String,
    prev : Double
) = if(text.matches(Regex(FLOAT_REGEX))) {
    try {
        java.lang.Double.parseDouble(text)
    } catch(_ : Throwable) {
        prev
    }
} else {
    prev
}