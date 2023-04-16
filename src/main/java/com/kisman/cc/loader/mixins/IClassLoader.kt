package com.kisman.cc.loader.mixins

/**
 * @author _kisman_
 * @since 11:35 of 13.04.2023
 */
interface IClassLoader {
    @Throws(ClassNotFoundException::class)
    fun findClass(
        name : String
    ) : Class<*>
}