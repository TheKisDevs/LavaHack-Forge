package com.kisman.cc.loader.antidump

import com.kisman.cc.loader.mixins.IClassLoader
import net.minecraft.launchwrapper.Launch.classLoader
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
//@Suppress("UNCHECKED_CAST")
@Suppress("ANNOTATION_TARGETS_NON_EXISTENT_ACCESSOR")
class CustomClassLoader(
    private val lavahackCache : Map<String, ByteArray>,
    private val lavahackMixinCache : Map<String, ByteArray>
//    parent : ClassLoader
) : ClassLoader(
//) : LaunchClassLoader(
//    emptyArray()

) {
    private val packages = listOf(
        "com.kisman.cc.",
        "the.kis.devs.api.",
        "lavahack.client",
        "lavahack.loader",
        "org.cubic.",
        "me.zero.alpine.",
        "ghost.classes.",
        "org.luaj.vm2.",
        "baritone."
    )

    @get:JvmName("get0")
    private val parent : ClassLoader

    private val runTransformers = Class
        .forName("net.minecraft.launchwrapper.LaunchClassLoader")
        .getDeclaredMethod("runTransformers", String::class.java, String::class.java, ByteArray::class.java).also {
            it.isAccessible = true
        }

    private val findClass = ClassLoader::class.java
        .getDeclaredMethod("findClass", String::class.java).also {
            it.isAccessible = true
        }

//    private val exclusions : Set<String>

    init {
        for(`package` in packages) {
            classLoader.addClassLoaderExclusion(`package`)
        }
        parent = Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("parent").also {
                it.isAccessible = true
            }[classLoader] as ClassLoader

//        println(parent0.javaClass.simpleName)

        Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("parent").also {
                it.isAccessible = true
            }[classLoader] = this

        /*println(Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("parent").also {
                it.isAccessible = true
            }[classLoader].javaClass.simpleName)*/

        /*exclusions = Class
            .forName("net.minecraft.launchwrapper.LaunchClassLoader")
            .getDeclaredField("classLoaderExceptions").also {
                it.isAccessible = true
            }[classLoader] as Set<String>*/

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
    /*init {
        javaClass.getDeclaredField("transformers").also {
            it.isAccessible = true
        }[this] = classLoader.transformers

        (javaClass.getDeclaredField("resourceCache").also {
            it.isAccessible = true
        }[this] as ConcurrentHashMap<String, ByteArray>).putAll(lavahackCache)
//        transformers = classLoader.transformers
    }*/
    private val lavahackClassCache = mutableMapOf<String, Class<*>>()
//    private var flag = false
    //    private val transformer = DeobfuscationTransformer()


    override fun loadClass(
        name : String
    ) : Class<*> {
        /*if(name.startsWith("net.minecraft.")&& !name.startsWith("net.minecraft.launchwrapper.")) {
            return classLoader.(name)
        }*/

//        if(name.startsWith("net.minecraftforge.")) {
//            println(name)
//            return (parent as IClassLoader).findClass(name)
//            return findClass.invoke(parent, name) as Class<*>
//        }
        /*if(name.startsWith("net.minecraft")) {
            println(name)
            return classLoader.findClass(name)
        }*/

        /*if(lavahackCache.contains(name)) {
            return findClass0(name)
        }*/
//        println("calling loadClass for $name")
    
        for(`package` in packages) {
            if(name.startsWith(`package`)) {
//                println("calling findClass for $name")
                return super.loadClass(name)
            }
        }


        try {
            return parent.loadClass(name)
//            return findClass.invoke(parent, name) as Class<*>
        } catch(_ : Throwable) {
//            println(name)
            /*println(ArrayList(Class.forName("net.minecraft.launchwrapper.LaunchClassLoader")
                .getDeclaredField("classLoaderExceptions").also {
                    it.isAccessible = true
                }[classLoader] as Set<String>).joinToString(","))

            for(string in Class.forName("net.minecraft.launchwrapper.LaunchClassLoader")
                .getDeclaredField("classLoaderExceptions").also {
                    it.isAccessible = true
                }[classLoader] as Set<String>) {
                if(name.startsWith(string)) {
                    println("valid $string")

                } else {
                    println("no valid $string")
                }
            }
            *//*for(exception in Class.forName("net.minecraft.launchwrapper.LaunchClassLoader")
                .getDeclaredField("classLoaderExceptions").also {
                    it.isAccessible = true
                }[classLoader] as Set<String>) {
                
            }*//*
            throw throwable*/
//            try {
                return classLoader.findClass(name)
//            } catch(_ : Throwable) {
//                return super.loadClass(name)
//            }
        }

//        flag = true
//        return super.loadClass(name)
    }

    /*public override fun findClass(
        name : String
    ) = super.findClass(name)*/

    public override fun findClass(
        name : String
    ) : Class<*> {
//        println(name)
//        if(name.startsWith("net.minecraftforge.")) {
//            println(name)
//            return parent.loadClass(name)
//        }

        for(`package` in packages) {
            if(name.startsWith(`package`)) {
                if(!lavahackClassCache.contains(name)) {
                    val bytes = runTransformers.invoke(classLoader, name, name, if(name.contains("mixin")) {
                        lavahackMixinCache
                    } else {
                        lavahackCache
                    }[name] ?: throw ClassNotFoundException("Class \"$name\" is not contained in cache of LavaHack")) as ByteArray

                    lavahackClassCache[name] = defineClass(name, bytes, 0, bytes.size).also {
//                        resolveClass(it)
                    }
                }

                return lavahackClassCache[name]!!
            }
        }

        try {
            return super.findClass(name)
        } catch(_ : Exception) {
            throw ClassNotFoundException("Class \"$name\" is not a part of LavaHack or plugin")
        }

    }/*if(lavahackCache.contains(name)) {
//        println(name)
        if(!lavahackClassCache.contains(name)) {
            val bytes0 = lavahackCache[name]!!
//            val bytes1 = defineClass(name, bytes0, 0, bytes0.size)
            *//*val bytes2 = classLoader.javaClass
                .getDeclaredMethod("runTransformers", String::class.java, String::class.java, ByteArray::class.java)
                .also {
                    it.isAccessible = true
                }
                .invoke(classLoader, name, name, lavahackCache[name]!!) as ByteArray*//*
//            val bytes3 = transformer.transform(name, name, lavahackCache[name]!!)!!

            *//*if(bytes3.contentEquals(lavahackCache[name]!!)) {
                println("same class $name")
            } else {
                println("remapped $name")
            }*//*

            lavahackClassCache[name!!] = defineClass(name, bytes0, 0, bytes0.size).also {
                resolveClass(it)
            }
        }

        lavahackClassCache[name]!!
    } else {
//        println(name)
//        parent0.findClass(name)
        throw ClassNotFoundException("Class \"$name\" is not a part of LavaHack")
    }*/
}