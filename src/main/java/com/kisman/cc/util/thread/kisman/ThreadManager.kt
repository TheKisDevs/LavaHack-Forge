package com.kisman.cc.util.thread.kisman

import com.kisman.cc.Kisman
import com.kisman.cc.util.nullCheck
import com.kisman.cc.util.stackTrace
import java.util.*

/**
 * @author _kisman_
 * @since 10:37 of 31.10.2022
 */

val processes = ArrayDeque<Runnable>()

class ThreadManager {
    val service = Service().also {
        it.name = "LavaHack-Thread-Manager"
        it.isDaemon = true
        it.start()
    }


    fun submit(
        runnable : Runnable
    ) {
        processes.add(runnable)
    }
}

class Service : Thread() {
    override fun run() {
        while(!currentThread().isInterrupted) {
            try {
                if(nullCheck()) {
                    if (processes.isNotEmpty()) {
                        processes.poll().run()
                    }

                    for (module in Kisman.instance.moduleManager.modules) {
                        try {
                            if (module.isToggled) {
                                module.thread()
                            }
                        } catch (e : Exception) {
                            stackTrace(e)
                        }
                    }
                } else {
                    yield()
                }
            } catch(e : Exception) {
                stackTrace(e)
            }
        }
    }
}