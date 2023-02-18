package com.kisman.cc.loader.antidump

/**
 * @author _kisman_
 * @since 13:50 of 31.08.2022
 */
class CustomClassLoader(
    private val lavahackCache : Map<String, ByteArray>
) : ClassLoader() {
    public override fun findClass(
        name : String?
    ) : Class<*> = if(lavahackCache.contains(name)) {
        val bytes = lavahackCache[name]!!

        defineClass(name, bytes, 0, bytes.size)
    } else {
        super.findClass(name)
    }
}