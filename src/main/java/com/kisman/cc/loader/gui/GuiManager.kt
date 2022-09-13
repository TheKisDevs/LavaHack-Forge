package com.kisman.cc.loader.gui

import com.kisman.cc.loader.Utility
import com.kisman.cc.loader.load
import com.kisman.cc.loader.versions

/**
 * @author _kisman_
 * @since 0:44 of 03.09.2022
 */

fun onLogin(
    key : String,
    version : String
) {
    if(key.isNotEmpty()) {
        Thread {
            load(
                Utility.cleaner(key),
                com.kisman.cc.loader.version,
                Utility.properties(),
                Runtime.getRuntime().availableProcessors().toString(),
                Utility.stringFixer(version)
            )
        } .start()
    }
}

fun getVersions() : Array<String> = versions