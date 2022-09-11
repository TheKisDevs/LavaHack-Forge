package com.kisman.cc.util

import com.kisman.cc.Kisman

/**
 * @author _kisman_
 * @since 19:43 of 11.09.2022
 */
object AccountData {
    var key : String? = null
    var properties : String? = null

    @JvmStatic fun check() {
        if (
            key == null
            ||
            properties != properties()
        ) {
//            Kisman.unsafeCrash()
        }
    }
}