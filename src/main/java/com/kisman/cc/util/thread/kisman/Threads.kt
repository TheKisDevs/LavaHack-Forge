package com.kisman.cc.util.thread.kisman

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * @author _kisman_
 * @since 11:34 of 24.02.2023
 */

val factory : ThreadFactory
    get() = ThreadFactoryBuilder().setDaemon(true).setNameFormat("LavaHack-Thread-%d").build()

val executor : ExecutorService
    get() = Executors.newCachedThreadPool(factory)

val defaultScope = CoroutineScope(Dispatchers.Default)