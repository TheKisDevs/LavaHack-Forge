package com.kisman.cc.loader.antidump

import sun.misc.Unsafe

/**
 * @author _kisman_
 * @since 12:40 of 04.07.2022
 */
val unsafe: Unsafe by lazy {
    Unsafe::class.java.getDeclaredField("theUnsafe").let {
        it.isAccessible = true
        it[null] as Unsafe
    }
}