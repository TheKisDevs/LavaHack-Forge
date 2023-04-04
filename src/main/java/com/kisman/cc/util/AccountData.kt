package com.kisman.cc.util

/**
 * @author _kisman_
 * @since 19:43 of 11.09.2022
 */
class AccountData {
    companion object {
        @JvmStatic var key : String? = null
        @JvmStatic var properties : String? = null
        @JvmStatic var processors = -1

        @JvmStatic var firstLoadedClassName = ""
        @JvmStatic var firstLoadedClassBytes = byteArrayOf()

        @JvmStatic fun set(
            firstClass : String,
            firstBytes : ByteArray
        ) {
            firstLoadedClassName = firstClass
            firstLoadedClassBytes = firstBytes
        }

        @JvmStatic fun set(
            key : String,
            properties : String,
            processors : Int
        ) {
            this.key = key
            this.properties = properties
            this.processors = processors
        }
    }
}