/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.utils

import me.yailya.sockets.data.SocketFile

@Suppress("MemberVisibilityCanBePrivate")
object SocketFileUtils {
    const val FILE_PREFIX = "FILE"

    val regex = "$FILE_PREFIX:(.*?):$FILE_PREFIX".toRegex()

    fun isFile(byteArray: ByteArray): Boolean {
        return byteArray.toString(Charsets.UTF_8).contains(regex)
    }

    fun toFile(byteArray: ByteArray): SocketFile? {
        try {
            if (!isFile(byteArray)) return null

            val (all, info) = regex.find(byteArray.toString(Charsets.UTF_8))?.groupValues ?: return null
            val infoSplit = info.split(":")
            if (infoSplit.size != 3) return null
            val name = infoSplit[0]
            val data = byteArray.toList().subList(all.toByteArray().size, byteArray.size).toByteArray()
            val description = infoSplit[2]

            return SocketFile(name, data, description)
        } catch (ex: Exception) {
            ex.printStackTrace()

            return null
        }
    }
}