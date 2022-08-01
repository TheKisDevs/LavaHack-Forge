@file:Suppress("NON_EXHAUSTIVE_WHEN", "UNCHECKED_CAST")

package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.gui.controller.GuiRoot
import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage.Type.*
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.random.Random

/**
 * @author _kisman_
 * @since 12:33 of 04.07.2022
 */

private const val address = "127.0.0.1"
private const val port = 1234

const val version = "1.0"

val client = SocketClient(address, port)

var answer : String? = null

var status = "Idling"

var loaded = false

private const val validAnswer = "2"

fun load(
    key : String,
    version : String,
    properties : String,
    processors : String
) {
    if(Utility.runningFromIntelliJ()) {
        Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        return
    }

    var haveJar = false
    var haveValidAnswer = false

    var bytes : ByteArray? = null

    var state = 0

    var needToBreak = false

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                if(state == 1) {
                    when (it.text) {
                        "0" -> {
                            status = "Invalid arguments of \"getpublicjar\" command!"
                            needToBreak = true
                        }
                        "1" -> {
                            status = "Invalid key or HWID | Loader is outdated!"
                            needToBreak = true
                        }
                        "2" -> status = "Valid!"
                    }
                }
            }
            File -> {
                bytes = it.file?.byteArray
            }
            Bytes -> {
                bytes = it.byteArray
            }
        }
    }


    state = 1
    client.writeMessage { text = "getpublicjar $key $version $properties $processors" }

    println("LavaFalcon is downloading classes...")

    status = "Waiting for LavaHack"

    while(client.connected) {
        if(bytes != null) {
            loadIntoResourceCache(bytes!!)
            bytes = null
            loaded = true
            LavaFalconCoreMod.resume()
            break
        }

        if(needToBreak) {
            break
        }
    }

    state = 2
}

fun createGui() {
    println("Creating the gui")
    GuiRoot.main()
}

fun initLoader() {
    Thread {
        setupServer()
        versionCheck(version)
        createGui()
    } .start()
}

fun setupServer() {
    client.connect()
    client.writeMessage { text = "LavaHack-Client" }
}

fun versionCheck(version : String) {
    println("VersionCheck was started!")

    var answer : String? = null

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                answer = it.text!!
                println("VersionCheck get raw answer($answer)")
            }
        }
    }

    client.writeMessage { text = "checkversion $version" }

    while(client.connected) {
        println("meow")
        if(answer != null) {
            break
        }
    }

    when (answer) {
        "0" -> status = "Invalid arguments of \"checkversion\" command!"
        "1" -> status = "Your loader is outdated! Please update it!"
        "2" -> status = "Loader is nice!"
    }

    println("VersionCheck: answer is $status")
}

fun loadIntoClassLoader(bytes : ByteArray) {
    val tempFile = File.createTempFile("LavaHack", ".jar")
    tempFile.writeBytes(bytes)
    tempFile.deleteOnExit()
    Launch.classLoader.addURL(tempFile.toURI().toURL())
}

fun loadIntoResourceCache(bytes : ByteArray) {
    val resourceCacheField = LaunchClassLoader::class.java.getDeclaredField("resourceCache")
    resourceCacheField.isAccessible = true
    val resourceCache = resourceCacheField[Launch.classLoader] as MutableMap<String, ByteArray>
    val resources = HashMap<String, ByteArray>()

    println("LavaFalcon is injecting classes...")

    status = "Injecting classes..."

    var classesCount = 0
    var resourcesCount = 0

    ZipInputStream(bytes.inputStream()).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                println("Injecting class \"${name.removeSuffix(".class")}\"")
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                resourceCache[name] = zipStream.readBytes()
                classesCount++
                status = "Injected ${name.split(".")[name.split("").size - 1]} class."
            } else if(Utility.validResource(name)) {
                println("Found new resource \"$name\"")
                resources[name] = Utility.getBytesFromInputStream(zipStream)
                resourcesCount++
                status = "Found \"$name\" resource."
            }
        }
    }

    println("Injected $classesCount classes, Found $resourcesCount resources")

    println("LavaFalcon is injecting resources...")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("lavahackResources-${Random(5000)}", ".jar")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        for(entry in resources.entries) {
            status = "Injecting \"${entry.key}\" resource."
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
            //classCache.put(entry.key, entry.value)
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        Launch.classLoader.addURL(tempFile.toURI().toURL())
    }

    println("LavaFalcon is setting resourceCache!")
    status = "Setting \"resourceCache\""

    resourceCacheField[Launch.classLoader] = resourceCache

    status = "Done!"

    println("LavaFalcon is done!")
}