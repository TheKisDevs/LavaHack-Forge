@file:Suppress("NON_EXHAUSTIVE_WHEN")

package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.antidump.CookieFuckery
import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketFile
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

    println("LavaFalcon is downloading classes...")

    @Suppress("UNCHECKED_CAST")
    val resourceCache = LaunchClassLoader::class.java.getDeclaredField("resourceCache").let {
        it.isAccessible = true
        it[Launch.classLoader] as MutableMap<String, ByteArray>
    }

    val client = SocketClient(address, port)

    var lastAnswer = ""
    var lastFile : SocketFile? = null

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                lastAnswer = it.text!!
            }
            File -> {
                lastFile = it.file!!
            }
        }
    }

    client.connect()
    client.writeMessage { text = "LavaHack-Client" }
    client.writeMessage { text = "getpublicjar" }

    var waitingForFile = false

    while(true) {
        if(lastAnswer == "2") {
            waitingForFile = true
        }

        if(waitingForFile) {
            if(lastFile != null) {
                if(lastFile?.name == "publicJar.jar" && lastFile?.description == "LavaHack") {
                    break
                }
            }
        }
    }

    val resources = HashMap<String, ByteArray>()

    ZipInputStream(lastFile?.byteArray?.inputStream()!!).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                resourceCache[name] = zipStream.readBytes()
            } else if(Utility.allowedFileSuffixes.contains(name.split(".")[name.split(".").size - 1])) {
                resources[name] = Utility.getBytesFromInputStream(zipStream)
            }
        }
    }

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
}