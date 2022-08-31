package com.kisman.cc.loader

import net.minecraft.launchwrapper.LaunchClassLoader
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
class CustomClassLoader(
    sources : Array<out URL>?
) : LaunchClassLoader(
    sources
) {
    val lavahackCache = ConcurrentHashMap<String, ByteArray>(1000)

    override fun getClassBytes(
        name : String?
    ) : ByteArray {
        return if(lavahackCache.contains(name)) lavahackCache[name]!! else super.getClassBytes(name)
    }
}