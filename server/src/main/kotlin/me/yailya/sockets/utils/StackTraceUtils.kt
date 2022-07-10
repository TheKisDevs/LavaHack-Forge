/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.utils

import me.yailya.sockets.server.SocketServerConnection

object StackTraceUtils {
    fun getStackTrace(throwable : Throwable, connection : SocketServerConnection?) : String {
        val stackTrace = StringBuilder()

        var throwableType = "Throwable";

        run {
            var superclass = throwable.javaClass.superclass

            while(superclass != null) {
                if(superclass == java.lang.Exception::class.java) {
                    throwableType = "Exception"
                    break
                } else if(superclass == java.lang.Error::class.java) {
                    throwableType = "Error"
                    break
                }

                superclass = superclass.superclass
            }
        }

        stackTrace.append("$throwableType in thread \"${Thread.currentThread().name}\" ")

        if(connection != null) {
            stackTrace.append("in socket \"${connection.name}\" ")
        }

        stackTrace.append(throwable.javaClass.name).append(": ").append(throwable.message).append("\n")
        for (i in throwable.stackTrace.indices) {
            stackTrace.append("\tat ").append(throwable.stackTrace[i].toString())
                .append(if (i != throwable.stackTrace.size - 1) "\n" else "")
        }
        return stackTrace.toString()
    }
}