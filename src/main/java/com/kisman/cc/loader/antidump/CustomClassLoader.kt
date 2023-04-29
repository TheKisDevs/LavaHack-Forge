package com.kisman.cc.loader.antidump

import net.minecraft.launchwrapper.Launch.classLoader
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.net.URL

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
@Suppress("ANNOTATION_TARGETS_NON_EXISTENT_ACCESSOR")
class CustomClassLoader(
    private val lavahackCache : Map<String, ByteArray>,
    private val lavahackMixinCache : Map<String, ByteArray>
) : ClassLoader() {
    private val packages = listOf(
        "com.kisman.cc.",
        "the.kis.devs.api.",
        "lavahack.client",
        "lavahack.loader",
        "org.cubic.",
        "me.zero.alpine.",
        "ghost.classes.",
        "org.luaj.vm2.",
        "baritone.",
        "com.viaversion.",
        "us.myles."
    )

    @get:JvmName("get0")
    private val parent : ClassLoader

    init {
        for(`package` in packages) {
            classLoader.addClassLoaderExclusion(`package`)
        }
        parent = Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("parent").also {
                it.isAccessible = true
            }[classLoader] as ClassLoader

        Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("parent").also {
                it.isAccessible = true
            }[classLoader] = this

        Class
            .forName("java.lang.ClassLoader")
            .getDeclaredField("parent").also { it0 ->
                it0.isAccessible = true

                Field::class.java
                    .getDeclaredField("modifiers").also { it1 ->
                        it1.isAccessible = true
                    }[it0] = it0.modifiers and Modifier.FINAL.inv()
            }[this] = parent

        Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("DEBUG").also {it0 ->
                it0.isAccessible = true

                Field::class.java
                    .getDeclaredField("modifiers").also { it1 ->
                        it1.isAccessible = true
                    }[it0] = it0.modifiers and Modifier.FINAL.inv()
            }[null] = true
    }

    private val lavahackClassCache = mutableMapOf<String, Class<*>>()

    override fun loadClass(
        name : String
    ) : Class<*> {
        for(`package` in packages) {
            if(name.startsWith(`package`)) {
                return super.loadClass(name)
            }
        }


        return try {
            parent.loadClass(name)
        } catch(_ : Throwable) {
            classLoader.findClass(name)
        }
    }

    public override fun findClass(
        name : String
    ) : Class<*> {
        for(`package` in packages) {
            if(name.startsWith(`package`)) {
                if(!lavahackClassCache.contains(name)) {
                    val bytes = if(name.contains("mixin")) {
                        lavahackMixinCache
                    } else {
                        lavahackCache
                    }[name] ?: throw ClassNotFoundException("Class \"$name\" is not contained in cache of LavaHack")

                    lavahackClassCache[name] = defineClass(name, bytes, 0, bytes.size)
                }

                return lavahackClassCache[name]!!
            }
        }

        try {
            return super.findClass(name)
        } catch(_ : Exception) {
            throw ClassNotFoundException("Class \"$name\" is not a part of LavaHack or plugin")
        }

    }

    override fun findResource(
        name : String
    ) : URL? = classLoader.findResource(name)
}