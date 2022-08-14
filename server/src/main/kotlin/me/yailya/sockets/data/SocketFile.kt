/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.data

import me.yailya.sockets.Constants
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class SocketFile(val name: String, val byteArray: ByteArray, val description: String) {
    val data get() = "${Constants.FILE_PREFIX}:$name:$description:${Constants.FILE_PREFIX}".toByteArray() + byteArray

    constructor(file: File, description: String = "") : this(file.name, file.readBytes(), description)
}