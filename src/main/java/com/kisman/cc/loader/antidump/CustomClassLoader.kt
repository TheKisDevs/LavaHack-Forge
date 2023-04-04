package com.kisman.cc.loader.antidump

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
class CustomClassLoader(
    private val lavahackCache : Map<String, ByteArray>
) : ClassLoader() {
    private val lavahackClassCache = mutableMapOf<String, Class<*>>()
    public override fun findClass(
        name : String?
    ) : Class<*> = if(lavahackCache.contains(name)) {
        if(!lavahackClassCache.contains(name)) {
            val bytes = lavahackCache[name]!!

            lavahackClassCache[name!!] = defineClass(name, bytes, 0, bytes.size).also {
//                resolveClass(it)
            }
        }

        lavahackClassCache[name]!!
    } else {
        super.findClass(name)
    }
}