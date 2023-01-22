@file:Suppress("UNCHECKED_CAST")

package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.LavaHackLoaderCoreMod.Companion.loaded
import com.kisman.cc.loader.antidump.CustomClassLoader
import com.kisman.cc.loader.antidump.initProvider
import com.kisman.cc.loader.antidump.runScanner
import com.kisman.cc.loader.gui.*
import com.kisman.cc.loader.websockets.IMessageProcessor
import com.kisman.cc.loader.websockets.WebClient
import com.kisman.cc.loader.websockets.data.SocketMessage
import com.kisman.cc.loader.websockets.setupClient
import com.kisman.cc.util.AccountData
import net.minecraft.launchwrapper.Launch.classLoader
import net.minecraft.launchwrapper.LaunchClassLoader
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @author _kisman_
 * @since 12:33 of 04.07.2022
 */

const val address = "161.97.78.143"
const val port = 25563

const val version = "2.0"

var loaded = false
var versions = emptyArray<String>()

var oldLogs = ArrayList<String>()

var overwritingLibrary = false
var haveLibraries = false

var canPressInstallButton = true

var receivedVersionCheckAnswer = false
var receivedVersions = false

var status = "Idling"
    set(value) {
        if(created) {
            log(value)
        } else {
            oldLogs.add(value)
        }
        field = value
    }

fun load(
    key : String,
    version : String,
    properties : String,
    processors : String,
    versionToLoad : String
) {
    if(Utility.runningFromIntelliJ()) {
        Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        return
    }

    if(!canPressInstallButton || loaded) {
        return
    }

    var client : WebClient? = null

    fun processBytes(
        bytes : ByteArray
    ) {
        status = "Successfully received LavaHack"
        loaded = true
        canPressInstallButton = false

        loadIntoResourceCache(bytes)
        close()
        client!!.close()
        AccountData.key = key
        AccountData.properties = properties
        AccountData.processors = processors.toInt()
        LavaHackLoaderCoreMod.resume()
    }

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            status = when (message) {
                "0" -> "Invalid arguments of \"getpublicjar\" command!"
                "1" -> "Invalid key or HWID or Loader is outdated!"
                "2" -> "Key and HWID is valid!"
                "3" -> "You have no access for selected version!"
                "4" -> "You have tried to dump/Already dumped LavaHack"
                else -> "Invalid answer of \"getpublicjar\" command"
            }
        }

        override fun processMessage(
            buff : ByteBuffer
        ) {
            processBytes(SocketMessage(buff.array()).file!!.byteArray)
        }

    }

    client = setupClient(messageProcessor)
    client.send("getpublicjar $key $version $properties $processors $versionToLoad")

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is trying to download classes...")

    status = "Trying to download LavaHack"
}

fun createGui() {
    LavaHackLoaderCoreMod.LOGGER.info("Creating the gui")

    create()

    for (log in oldLogs) {
        log(log)
    }
}

fun initLoader() {
    initProvider()

    thread {
        try {
            runScanner()
            downloadLibraries()
            versionCheck(version)

            while(true) {
                if(receivedVersionCheckAnswer) {
                    break
                }

                Thread.sleep(1000 * 5)
            }

            versions(version)

            while(true) {
                if(receivedVersions) {
                    break
                }

                Thread.sleep(1000 * 5)
            }

            createGui()
        } catch(e : Exception) {
            LavaHackLoaderCoreMod.LOGGER.info("Error Code: 0x", e)
            Utility.unsafeCrash()
        }
    }
}

private fun downloadLibraries() {
    if(!haveLibraries) {
        return
    }

    val folder = File("lavahack/loader/libraries")
    val library = File("lavahack/loader/libraries/library.jar")

    if(library.exists() && !overwritingLibrary) {
        return
    }

    /*val client = SocketClient(
        address,
        port
    )*/

    var librariesCount = 0
    var receivedLibraries = 0
    var bytes : ByteArray? = null

    /*client.onMessageReceived = {
        when(it.type) {
            Text -> {
                librariesCount = Integer.parseInt(it.text!!)
            }
            File -> {
                bytes = it.file?.byteArray
                receivedLibraries++
            }
            Bytes -> {
                bytes = it.byteArray
                receivedLibraries++
            }
        }
    }*/

    status = "Started downloading libraries"

//    setupSocketClient(client)

//    client.writeMessage { text = "getlibraries" }

    if(!folder.exists()) {
        Files.createFile(folder.toPath())
    }

    /*while(client.connected) {
        if(bytes != null) {
            if(library.exists()) {
                library.delete()
            }

            Files.createFile(library.toPath())
            Files.write(library.toPath(), bytes!!)

            status = "Received library"

            break
        }

        *//*if(receivedLibraries >= librariesCount) {
            break
        }*//*
    }*/

    loadIntoClassLoader(Files.readAllBytes(library.toPath()))

    status = "Loaded libraries into class loader"
}

fun versionCheck(version : String) {
    LavaHackLoaderCoreMod.LOGGER.info("VersionCheck was started!")

    var client : WebClient? = null

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            LavaHackLoaderCoreMod.LOGGER.info("VersionCheck: raw answer is \"$message\"")

            status = when (message) {
                "0" -> "Invalid arguments of \"checkversion\" command!"
                "1" -> "Your loader is outdated! Please update it!"
                "2" -> "Loader is on latest version!"
                else -> "kill yourself <3"
            }

            LavaHackLoaderCoreMod.LOGGER.info(status)

            if (message != "2") {
                Utility.unsafeCrash()
            }

            receivedVersionCheckAnswer = true

            client!!.close()
        }

        override fun processMessage(
            buff : ByteBuffer
        ) { }
    }

    client = setupClient(messageProcessor)
    client.send("checkversion $version")
}

fun versions(version : String) {
    LavaHackLoaderCoreMod.LOGGER.info("VersionsList was started!")

    var client : WebClient? = null

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            LavaHackLoaderCoreMod.LOGGER.info("VersionsList: raw answer is \"$message\"")
            when (message) {
                "0" -> status = "Invalid arguments of \"getversions\" command!"
                "1" -> status = "Invalid loader version!"
                else -> {
                    if(message.startsWith("2")) {
                        status = "Successfully received version list"
                        versions = message.split("|")[1].split("&").toTypedArray()
                        receivedVersions = true
                    }
                }
            }

            LavaHackLoaderCoreMod.LOGGER.info(status)

            if(status != "Successfully received version list") {
                Utility.unsafeCrash()
            }

            client!!.close()
        }

        override fun processMessage(
            buff : ByteBuffer
        ) { }

    }

    client = setupClient(messageProcessor)
    client.send("getversions $version")
}

fun loadIntoClassLoader(bytes : ByteArray) {
    val tempFile = File.createTempFile("LavaHack-Main-Class", ".jar")
    tempFile.writeBytes(bytes)
    tempFile.deleteOnExit()
    classLoader.addURL(tempFile.toURI().toURL())
}

fun loadIntoResourceCache(bytes : ByteArray) {
    val resourceCacheField = LaunchClassLoader::class.java.getDeclaredField("resourceCache")
    resourceCacheField.isAccessible = true
    val resourceCache = resourceCacheField[classLoader] as MutableMap<String, ByteArray>
    val resources = HashMap<String, ByteArray>()

    LavaHackLoaderCoreMod.LOGGER.info("Injecting classes...")

    status = "Injecting classes..."

    var classesCount = 0
    var resourcesCount = 0

    var firstClassName : String? = null
    var firstClassBytes : ByteArray? = null

    ZipInputStream(bytes.inputStream()).use { zipStream ->
        var zipEntry : ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                LavaHackLoaderCoreMod.LOGGER.info("Injecting class \"${name.removeSuffix(".class")}\"")
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                if(name == "Main") {
                    loadIntoClassLoader(zipStream.readBytes())
                } else {
                    resourceCache[name] = zipStream.readBytes()

                    if(firstClassName == null) {
                        firstClassName = name
                        firstClassBytes = resourceCache[name]
                    }
                }

                classesCount++
                status = "Injecting $name"
            } else if(Utility.validResource(name)) {
                LavaHackLoaderCoreMod.LOGGER.info("Found new resource \"$name\"")
                resources[name] = Utility.getBytesFromInputStream(zipStream)
                resourcesCount++
                status = "Found \"$name\" resource."
            }
        }
    }

    LavaHackLoaderCoreMod.LOGGER.info("Injected $classesCount classes, Found $resourcesCount resources")

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is injecting resources...")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("lavahackResources-${Random(5000)}", ".jar")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        for(entry in resources.entries) {
            status = "Injecting \"${entry.key}\" resource."
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        classLoader.addURL(tempFile.toURI().toURL())
    }

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is setting resourceCache!")
    status = "Setting \"resourceCache\""

    resourceCacheField[classLoader] = resourceCache

    status = "Done!"

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is done!")

    AccountData.firstLoadedClassName = firstClassName!!
    AccountData.firstLoadedClassBytes = firstClassBytes!!
}

fun loadIntoCustomClassLoader(
    bytes : ByteArray
) {
    val classes = ConcurrentHashMap<String, ByteArray>()
    val resources = HashMap<String, ByteArray>()

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is injecting LavaHack...")

    status = "Finding files..."

    var classesCount = 0
    var resourcesCount = 0

    ZipInputStream(bytes.inputStream()).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                LavaHackLoaderCoreMod.LOGGER.info("Found class \"${name.removeSuffix(".class")}\"")
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                classes[name] = zipStream.readBytes()

                classesCount++
                status = "Found class \"${name.removeSuffix(".class")}\""
            } else if(Utility.validResource(name)) {
                LavaHackLoaderCoreMod.LOGGER.info("Found resource \"$name\"")
                resources[name] = Utility.getBytesFromInputStream(zipStream)
                resourcesCount++
                status = "Found resource \"$name\""
            }
        }
    }

    LavaHackLoaderCoreMod.LOGGER.info("Found $classesCount classes and $resourcesCount resources")
    status = "Found $classesCount classes and $resourcesCount resources"

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is injecting classes...")
    status = "Injecting classes..."

    if(classes.isNotEmpty()) {
        val customClassLoader = CustomClassLoader(classLoader)

        customClassLoader.lavahackCache = classes

        for(`class` in classes) {
            loadClass(
                `class`.key,
                false
            ) {
                customClassLoader.findClass(it)
            }
        }
    }

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is injecting resources...")
    status = "Injecting resources..."

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("lavahackResources-${Random(5000)}", ".jar")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        for(entry in resources.entries) {
            LavaHackLoaderCoreMod.LOGGER.info("Injecting \"${entry.key}\" resource")
            status = "Injecting \"${entry.key}\" resource"
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        classLoader.addURL(tempFile.toURI().toURL())
    }

    status = "Successfully loader!"

    LavaHackLoaderCoreMod.LOGGER.info("LavaHack Loader is done!")
}

private fun loadClass(
    name : String,
    resolve : Boolean,
    classFinder : (String) -> Class<*>?
) : Class<*> {
    synchronized (
            /*getClassLoadingLock(name)*/
            Utility.invokeMethod<Any>(
                classLoader,
                "getClassLoadingLock",
                name
            )!!
    ) {
        // First, check if the class has already been loaded
        var c = Utility.invokeMethod<Class<*>>(
            classLoader,
            "findLoadedClass",
            name
        )//classLoader.findLoadedClass(name);
        if (c == null) {
            val time = System.nanoTime();
            val parent = Utility.field<ClassLoader>(
                classLoader,
                "parent"
            )

            try {
                c = if (parent != null) {
                    Utility.invokeMethod<Class<*>>(
                        classLoader,
                        "loadClass",
                        name,
                        false
                    )//parent.loadClass(name, false);
                } else {
                    Utility.invokeMethod(
                        classLoader,
                        "findBootstrapClassOrNull",
                        name
                    )//findBootstrapClassOrNull(name);
                }
            } catch (e : ClassNotFoundException) {
                // ClassNotFoundException thrown if class not found
                // from the non-null parent class loader
            }

            if (c == null) {
                // If still not found, then invoke findClass in order
                // to find the class.
                val timeNew = System.nanoTime();
                c = classFinder(
                    name
                )//classLoader.findClass(name);

                // this is the defining class loader; record the stats
                sun.misc.PerfCounter.getParentDelegationTime().addTime(timeNew - time);
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(timeNew);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
        }
        if (resolve) {
            Utility.invokeMethod<Void>(
                classLoader,
                "resolveClass",
                c
            )
            //classLoader.resolveClass(c);
        }
        return c;
    }
}