package com.kisman.cc.loader

import com.kisman.cc.Kisman
import com.kisman.cc.loader.cubic.CubicLoader
import com.kisman.cc.loader.LavaHackLoaderCoreMod.Companion.loaded
import com.kisman.cc.loader.antidump.CustomClassLoader
import com.kisman.cc.loader.antidump.initProvider
import com.kisman.cc.loader.antidump.runScanner
import com.kisman.cc.loader.gui.*
import com.kisman.cc.loader.websockets.IMessageProcessor
import com.kisman.cc.loader.websockets.WebClient
import com.kisman.cc.loader.websockets.data.SocketMessage
import com.kisman.cc.loader.websockets.setupClient
import net.minecraft.launchwrapper.Launch.classLoader
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraftforge.fml.common.FMLLog
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.transformer.ClassInfo
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread

/**
 * @author _kisman_
 * @since 12:33 of 04.07.2022
 */

const val address = "иди нахуй"
const val port = 25563

const val version = "2.1"

var loaded = false
var versions = emptyArray<String>()

var oldLogs = ArrayList<String>()

var overwritingLibrary = false
var haveLibraries = false

var canPressInstallButton = true

var receivedVersionCheckAnswer = false
var receivedVersions = false

var CUSTOM_CLASSLOADER : CustomClassLoader? = null

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
        message("Successfully received LavaHack")
        loaded = true
        canPressInstallButton = false

        loadCubic(bytes)
        close()
        client!!.close()
        //CUSTOM_CLASSLOADER!!
        //    .findClass("com.kisman.cc.util.AccountData")
        //    .getMethod("set", String::class.java, String::class.java, Int::class.java)
        //    .invoke(null, key, properties, processors.toInt())
        classLoader
            .findClass("com.kisman.cc.util.AccountData")
            .getMethod("set", String::class.java, String::class.java, Int::class.java)
            .invoke(null, key, properties, processors.toInt())
        LavaHackLoaderCoreMod.resume()
    }

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            message(when (message) {
                "0" -> "Invalid arguments of \"getpublicjar\" command!"
                "1" -> "Invalid key or HWID or Loader is outdated!"
                "2" -> "Key and HWID is valid!"
                "3" -> "You have no access for selected version!"
                "4" -> "You have tried to dump/Successfully dumped LavaHack"
                else -> "Invalid answer of \"getpublicjar\" command"
            })

            if(message == "3" || message == "1") {
                canPressInstallButton = true
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
    canPressInstallButton = false

    message("Trying to download LavaHack")
}

fun message(
    message : String
) {
    status = message
    LavaHackLoaderCoreMod.LOGGER.info(message)
}

fun createGui() {
    message("Creating the gui")
    create()

    for (log in oldLogs) {
        log(log)
    }
}

fun initLoader() {
    initProvider()
    swapLoggers()

    thread {
        try {
            runScanner()
            downloadLibraries()
            versionCheck(version)

            while(true) {
                if(receivedVersionCheckAnswer) {
                    break
                }

                Thread.sleep(1000)
            }

            versions(version)

            while(true) {
                if(receivedVersions) {
                    break
                }

                Thread.sleep(1000)
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
    message("Checking if your loader is on latest version!")

    var client : WebClient? = null

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            LavaHackLoaderCoreMod.LOGGER.info("VersionCheck: raw answer is \"$message\"")

            message(when (message) {
                "0" -> "Invalid arguments of \"checkversion\" command!"
                "1" -> "Your loader is outdated! Please update it!"
                "2" -> "Loader is on latest version!"
                else -> "CANT PARSE ANSWER OF VERSIONCHECK COMMAND(\"$message\")"
            })

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
    message("Getting version list")

    var client : WebClient? = null

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            LavaHackLoaderCoreMod.LOGGER.info("VersionsList: raw answer is \"$message\"")
            message(when (message) {
                "0" -> "Invalid arguments of \"getversions\" command!"
                "1" -> "Outdated loader!"
                else -> {
                    if(message.startsWith("2")) {
                        versions = message.split("|")[1].split("&").toTypedArray()
                        receivedVersions = true
                        "Successfully received version list"
                    } else {
                        "CANT PARSE ANSWER OF GETVERSIONS COMMAND(\"$message\")"
                    }
                }
            })

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

fun loadIntoCustomClassLoader(
    bytes : ByteArray
) {
    val resourceCacheField = LaunchClassLoader::class.java.getDeclaredField("resourceCache")
    resourceCacheField.isAccessible = true
//    val resourceCache = resourceCacheField[classLoader] as MutableMap<String, ByteArray>
    val classes = HashMap<String, ByteArray>()
    val mixins = HashMap<String, ByteArray>()
    val resources = HashMap<String, ByteArray>()

    LavaHackLoaderCoreMod.LOGGER.info("Injecting classes...")

    status = "Injecting classes..."

    var classesCount = 0
    var mixinsCount = 0
    var resourcesCount = 0

    var firstClassName = ""
    var firstClassBytes = byteArrayOf()

    val fromClassNode = ClassInfo::class.java
        .getDeclaredMethod("fromClassNode", ClassNode::class.java).also {
            it.isAccessible = true
        }

    /*val runTransformers = Class
        .forName("net.minecraft.launchwrapper.LaunchClassLoader")
        .getDeclaredMethod("runTransformers", String::class.java, String::class.java, ByteArray::class.java).also {
            it.isAccessible = true
        }*/

    Class
        .forName("net.minecraft.launchwrapper.LaunchClassLoader")
        .getDeclaredField("DEBUG_FINER").also {it0 ->
            it0.isAccessible = true

            Field::class.java
                .getDeclaredField("modifiers").also { it1 ->
                    it1.isAccessible = true
                }[it0] = it0.modifiers and Modifier.FINAL.inv()
        }[null] = true


    /*for(method in ClassInfo::class.java.declaredMethods) {
        println(method.name + " " + method.parameterTypes.joinToString { it.name })
    }*/

    /*val constructor = ClassInfo::class.java
        .getDeclaredConstructor(ClassNode::class.java).also {
            it.isAccessible = true
        }*/

//    val cache = mutableMapOf<String, ClassInfo>()

    ZipInputStream(bytes.inputStream()).use { stream ->
        var entry : ZipEntry?

        while (stream.nextEntry.also { entry = it } != null) {
            var name = entry!!.name

            if (name.endsWith(".class")) {
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                val bytes0 = stream.readBytes()//runTransformers.invoke(classLoader, name, name, stream.readBytes()) as ByteArray
                //ClassNode classNode = new ClassNode();
                //        ClassReader classReader = new ClassReader(classBytes);
                //        classReader.accept(classNode, flags);
                //        return classNode;
                val node = ClassNode().also { it0 ->
                    ClassReader(bytes0).also { it1 ->
                        it1.accept(it0, 0)
                    }
                }

//                val info = constructor.newInstance(node)

//                cache[node.name] = constructor.newInstance(node)

                fromClassNode.invoke(null, node)

                if(name.toLowerCase().contains("mixin")) {
                    mixins[name] = bytes0
                    mixinsCount++
                    message("Processing mixin $name")
                } else {
                    classes[name] = bytes0
                    classesCount++
                    message("Processing class $name")
                }

                if(firstClassName.isEmpty()) {
                    firstClassName = name
                    firstClassBytes = bytes0
                }
            } else if(Utility.validResource(name)) {
                resources[name] = Utility.getBytesFromInputStream(stream)
                resourcesCount++
                message("Processing resource $name")
            }
        }
    }

    message("Processed $classesCount classes, $mixinsCount mixins, $resourcesCount resources")
    message("Injecting $classesCount classes")

    CUSTOM_CLASSLOADER = CustomClassLoader(classes, mixins)

    message("Injecting $resourcesCount resources")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("${System.currentTimeMillis()}", ".lavahack")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        Files.setAttribute(tempFile.toPath(), "dos:hidden", true)

        for(entry in resources.entries) {
            message("Injecting \"${entry.key}\" resource.")
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        classLoader.addURL(tempFile.toURI().toURL())
    }

    message("Injecting $mixinsCount mixins")

    resourceCacheField[classLoader] = ConcurrentHashMap(mixins)

    message("Successfully loaded LavaHack")

    CUSTOM_CLASSLOADER!!
        .findClass("com.kisman.cc.util.AccountData")
        .getMethod("set", String::class.java, ByteArray::class.java)
        .invoke(null, firstClassName, firstClassBytes)
}

fun swapLoggers() {
    message("Swapping FML logger")

    FMLLog::class.java
        .getDeclaredField("log").also { it0 ->
            it0.isAccessible = true

            Field::class.java
                .getDeclaredField("modifiers").also { it1 ->
                    it1.isAccessible = true
                }[it0] = it0.modifiers and Modifier.FINAL.inv()
        }[null] = CustomFMLLogger()
}

/**
 * Work in progress :)
 * @author Cubic
 */
fun loadCubic(
    bytes : ByteArray
) {
    CubicLoader.init()

    val resourceCacheField = LaunchClassLoader::class.java.getDeclaredField("resourceCache")
    resourceCacheField.isAccessible = true
    val mixins = HashMap<String, ByteArray>()
    val resources = HashMap<String, ByteArray>()

    LavaHackLoaderCoreMod.LOGGER.info("Injecting classes...")

    status = "Injecting classes..."

    var mixinsCount = 0
    var resourcesCount = 0

    var firstClassName = ""
    var firstClassBytes = byteArrayOf()

    Class
        .forName("net.minecraft.launchwrapper.LaunchClassLoader")
        .getDeclaredField("DEBUG_FINER").also {it0 ->
            it0.isAccessible = true

            Field::class.java
                .getDeclaredField("modifiers").also { it1 ->
                    it1.isAccessible = true
                }[it0] = it0.modifiers and Modifier.FINAL.inv()
        }[null] = true

    ZipInputStream(bytes.inputStream()).use { stream ->
        var entry : ZipEntry?

        while (stream.nextEntry.also { entry = it } != null) {
            var name = entry!!.name

            if (name.endsWith(".class")) {
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                val bytes0 = stream.readBytes()

                CubicLoader.load(name, bytes0)

                if(firstClassName.isEmpty()) {
                    firstClassName = name
                    firstClassBytes = bytes0
                }
            } else if(Utility.validResource(name)) {
                resources[name] = Utility.getBytesFromInputStream(stream)
                resourcesCount++
                message("Processing resource $name")
            }
        }
    }

    message("Injecting $resourcesCount resources")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("${System.currentTimeMillis()}", ".lavahack")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        Files.setAttribute(tempFile.toPath(), "dos:hidden", true)

        for(entry in resources.entries) {
            message("Injecting \"${entry.key}\" resource.")
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        classLoader.addURL(tempFile.toURI().toURL())
    }

    message("Injecting $mixinsCount mixins")

    resourceCacheField[classLoader] = ConcurrentHashMap(mixins)

    message("Successfully loaded LavaHack")

    classLoader
        .findClass("com.kisman.cc.util.AccountData")
        .getMethod("set", String::class.java, ByteArray::class.java)
        .invoke(null, firstClassName, firstClassBytes)
}
