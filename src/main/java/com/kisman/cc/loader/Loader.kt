package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.antidump.CookieFuckery
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
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

private const val clientUrl = "C:/Users/Admin/AppData/Roaming/.minecraft/mods/crystal pvp/kisman.cc-b0.1.6.5-plus-release.jar"

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

    println("Falcon is downloading classes...")

    @Suppress("UNCHECKED_CAST")
    val resourceCache = LaunchClassLoader::class.java.getDeclaredField("resourceCache").let {
        it.isAccessible = true
        it[Launch.classLoader] as MutableMap<String, ByteArray>
    }

    val stream = File(clientUrl).toURL().openConnection().also {
        it.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
    }.getInputStream()

    val resources = HashMap<String, ByteArray>()

    ZipInputStream(stream).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                resourceCache[name] = zipStream.readBytes()
            } else {
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