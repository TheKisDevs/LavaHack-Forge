/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.data

import me.yailya.sockets.utils.SocketFileUtils.FILE_PREFIX
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class SocketFile(val name: String, val byteArray: ByteArray, val description: String = "") {
    val data get() = "$FILE_PREFIX:${name}:${byteArray.size}:${description}:$FILE_PREFIX".toByteArray() + byteArray

    constructor(file: File, description: String = "") : this(file.name, file.readBytes(), description)
}