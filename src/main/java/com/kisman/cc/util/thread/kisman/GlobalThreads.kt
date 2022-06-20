package com.kisman.cc.util.thread.kisman

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * @author _kisman_
 * @since 13:40 of 18.06.2022
 */
interface GlobalThreads {
    val factory : ThreadFactory
        get() = ThreadFactoryBuilder().setDaemon(true).setNameFormat("LavaHack-Thread-%d").build()

    val executor : ExecutorService
        get() = Executors.newCachedThreadPool(factory)
}