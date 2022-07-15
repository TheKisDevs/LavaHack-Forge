/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.sockets.utils

import com.kisman.cc.sockets.server.SocketServerConnection

@Suppress("unused")
object StackTraceUtils {
    fun getStackTrace(throwable: Throwable, connection: SocketServerConnection? = null): String {
        val stackTrace = StringBuilder()
        val throwableType = when (throwable) {
            is Exception -> "Exception"
            is Error -> "Error"
            else -> "Throwable"
        }

        stackTrace.append("$throwableType in thread \"${Thread.currentThread().name}\"")

        if (connection != null) {
            stackTrace.append(" in socket \"${connection.name}\"")
        }

        stackTrace.append(" ${throwable.javaClass.name}: ${throwable.message}\n")
        throwable.stackTrace.withIndex().forEach {
            stackTrace.append("\tat ${it.value}${if (it.index != throwable.stackTrace.lastIndex) "\n" else ""}")
        }

        return stackTrace.toString()
    }
}