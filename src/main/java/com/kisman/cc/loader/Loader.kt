@file:Suppress("NON_EXHAUSTIVE_WHEN", "UNCHECKED_CAST")

package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.antidump.CookieFuckery
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
 * TODO: I will rewrite it when i will have website or vds host
 *
 * @author _kisman_
 * @since 12:33 of 04.07.2022
 */

private const val address = "127.0.0.1"
private const val port = 1234

private const val validAnswer = "2"
fun load() {
    if(Utility.runningFromIntelliJ()) {
        Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        return
    }
    
    // NOTE: These WILL break when debug tools such as IntelliJ are attached to the process
    // THIS IS PURPOSEFUL
    CookieFuckery.checkLaunchFlags()
    CookieFuckery.disableJavaAgents()
    CookieFuckery.setPackageNameFilter()
    CookieFuckery.dissasembleStructs()

    val client = SocketClient(address, port)

    var haveJar = false
    var haveValidAnswer = false

    var answer : String? = null

    var bytes : ByteArray? = null

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                if(answer == null) {
                    answer = it.text!!
                    if (it.text!! == validAnswer) {
                        haveValidAnswer = true;
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

    client.connect()
    client.writeMessage { text = "LavaHack-Client" }
    client.writeMessage { text = "getpublicjar" }

    println("LavaFalcon is downloading classes...")

    while(client.connected) {
        if(bytes != null) {
            loadIntoClassLoader(bytes!!)

            break
        }
    }
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

    ZipInputStream(bytes.inputStream()).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                println("Injecting class \"${name.removeSuffix(".class")}\"")
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                resourceCache[name] = zipStream.readBytes()
            } else if(Utility.validResource(name)) {
                println("Found new resource \"$name\"")
                resources[name] = Utility.getBytesFromInputStream(zipStream)
            }
        }
    }

    println("LavaFalcon is injecting resources...")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("lavahackResources-${Random(5000)}", ".jar")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        for(entry in resources.entries) {
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

    resourceCacheField[Launch.classLoader] = resourceCache

    println("LavaFalcon is done!")
}