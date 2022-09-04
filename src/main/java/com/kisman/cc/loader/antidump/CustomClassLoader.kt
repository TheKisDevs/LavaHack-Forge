package com.kisman.cc.loader.antidump

import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
class CustomClassLoader(
    parent : ClassLoader
) : ClassLoader(
    parent
) {
    var lavahackCache = ConcurrentHashMap<String, ByteArray>()

    public override fun findClass(
        name : String?//like com.kisman.cc.Kisman
    ) : Class<*> {
        val transformedName = name!!.replace(".", "/") + ".class"
        val bytes = lavahackCache[transformedName]

        return if(lavahackCache.containsKey(name)) super.defineClass(name, bytes, 0, bytes!!.size) else super.findClass(name)
    }
}