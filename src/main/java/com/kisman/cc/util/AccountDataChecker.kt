package com.kisman.cc.util

import com.kisman.cc.Kisman
import com.kisman.cc.loader.Utility
import com.kisman.cc.websockets.IMessageProcessor
import com.kisman.cc.websockets.setupClient

/**
 * @author _kisman_
 * @since 14:24 of 07.01.2023
 */

private var runnables = mutableListOf<Runnable>()

fun check() {
    if(AccountData.key == null || AccountData.properties == null || AccountData.firstLoadedClassName.isEmpty() || AccountData.firstLoadedClassBytes.isEmpty()) {
        Kisman.LOGGER.error("Error Code: 0x0003")
        Kisman.unsafeCrash()
        return
    }

    try {
        resourceCache()[AccountData.firstLoadedClassName]
    } catch(e : Exception) {
        Kisman.LOGGER.error("Error Code: 0x0001")
        Utility.unsafeCrash()
    }

    val messageProcessor = object : IMessageProcessor {
        override fun processMessage(
            message : String
        ) {
            if(message == "2") {
                runnables.clear()
            } else {
                Kisman.LOGGER.error("Error Code: 0x0002")
                Kisman.unsafeCrash()
            }
        }
    }

    Runnable { setupClient(messageProcessor).send("auth ${AccountData.key} ${AccountData.properties} ${AccountData.processors}") }.also { runnables.add(it) }.run()
}