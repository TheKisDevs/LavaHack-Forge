package com.kisman.cc.util.thread.kisman

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * I(kisman) skidded this class from 3arthH4ck but.
 *
 * Original class: com.google.common.util.concurrent.ThreadFactoryBuilder
 *
 * So we are independent. Afaik the lib is licensed with Apache-2.0
 *
 * @author _kisman_
 * @since 13:48 of 18.06.2022
 */
@SuppressWarnings("UnusedReturnValue")
class ThreadFactoryBuilder {
    private var daemon : Boolean? = null
    private var nameFormat : String? = null

    fun setDaemon(daemon : Boolean) : ThreadFactoryBuilder {
        this.daemon = daemon;
        return this;
    }

    fun setNameFormat(nameFormat : String) : ThreadFactoryBuilder {
        this.nameFormat = nameFormat;
        return this;
    }

    fun build() : ThreadFactory {
        return ThreadFactory {
            val thread : Thread = Executors.defaultThreadFactory().newThread(it)
            if (daemon != null) {
                thread.isDaemon = daemon!!
            }
            if (nameFormat != null) {
                thread.name = String.format(Locale.ROOT, nameFormat!!, AtomicLong(0).getAndIncrement())
            }
            return@ThreadFactory thread
        }
    }
}
